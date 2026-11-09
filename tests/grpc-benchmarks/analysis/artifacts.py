"""Loading and grouping of saved benchmark trial artifacts."""

from __future__ import annotations

import json
import math
from statistics import median
import os
from dataclasses import dataclass, field
from typing import Any

TRIALS_DIR = "trials"

# Version 2 added the CPU and memory counters. Records of every version stay readable.
SUPPORTED_TRIAL_SCHEMA_VERSIONS = (1, 2)

# A latency percentile is only meaningful when the per-trial sample count
# reaches this bound. The analysis flags any smaller trial instead of
# silently reporting the maximum as the percentile.
MIN_SAMPLES_FOR_PERCENTILE = {
    "p50": 2,
    "p90": 10,
    "p95": 20,
    "p99": 100,
    "p999": 1000,
}


@dataclass
class Trial:
    """One parsed trial record plus the run-level context it belongs to."""

    record: dict[str, Any]
    run_id: str
    run_dir: str

    @property
    def implementation(self) -> str:
        return self.record["implementation"]

    @property
    def benchmark(self) -> str:
        return self.record["benchmark"]

    @property
    def case(self) -> str:
        return self.record["case"]

    @property
    def status(self) -> str:
        return self.record["status"]

    @property
    def parameters(self) -> dict[str, Any] | None:
        return self.record.get("parameters")

    @property
    def latency_metric(self) -> str:
        return self.record["latency_metric"]

    @property
    def operation_unit(self) -> str:
        return self.record["operation_unit"]

    @property
    def platform(self) -> str:
        return self.record["platform"]

    @property
    def target(self) -> str:
        return self.record["target"]

    @property
    def injected_delay_ms(self) -> int:
        return self.record.get("injected_delay_ms", 0)

    @property
    def elapsed_ns(self) -> int:
        return self.record["elapsed_ns"]

    def cpu_total_ns(self) -> int | None:
        """The CPU time of the measured phase, summed over all threads. None for schema v1 records."""
        return self.record.get("cpu_total_ns")

    def max_rss_bytes(self) -> int | None:
        return self.record.get("max_rss_bytes")

    @property
    def error(self) -> str | None:
        return self.record.get("error")

    def samples(self) -> list[int]:
        return self.record.get("latency_samples_ns", [])

    def events(self, name: str) -> list[int]:
        return self.record.get("events", {}).get(name, [])

    def counts(self, name: str) -> Any:
        return self.record.get("counts", {}).get(name)

    def key(self) -> tuple[str, str]:
        return (self.benchmark, self.case)

    def comparable_key(self, tolerate_injected_delay: bool = False) -> tuple | None:
        """The identity that two trials need before a comparison can pair them.

        Two runs with different injected delays never pair by accident. The
        positive-control workflow passes `tolerate_injected_delay` and states
        both delays in its output.
        """
        if self.status != "completed" or self.parameters is None:
            return None
        injected_delay = None if tolerate_injected_delay else self.injected_delay_ms
        return (
            self.benchmark,
            self.case,
            self.platform,
            self.target,
            injected_delay,
            tuple(sorted(self.parameters.items())),
        )


@dataclass
class Run:
    """One run directory: a manifest plus the trial records it references."""

    run_id: str
    run_dir: str
    manifest: dict[str, Any]
    trials: list[Trial] = field(default_factory=list)

    @property
    def implementations(self) -> list[str]:
        return sorted({trial.implementation for trial in self.trials})


class ArtifactError(Exception):
    """Raised when a run directory can't be read."""


def load_run(run_dir: str) -> Run:
    """Reads one run directory. Raises [ArtifactError] when the layout is invalid."""
    manifest_path = os.path.join(run_dir, "manifest.json")
    if not os.path.isfile(manifest_path):
        raise ArtifactError(f"'{run_dir}' has no manifest.json. Pass a run directory created by 'bench run'.")
    with open(manifest_path, encoding="utf-8") as file:
        manifest = json.load(file)

    run = Run(
        run_id=manifest.get("run_id", os.path.basename(run_dir)),
        run_dir=run_dir,
        manifest=manifest,
    )
    trials_dir = os.path.join(run_dir, TRIALS_DIR)
    if not os.path.isdir(trials_dir):
        return run

    for name in sorted(os.listdir(trials_dir)):
        if not name.endswith(".json"):
            continue
        path = os.path.join(trials_dir, name)
        try:
            with open(path, encoding="utf-8") as file:
                record = json.load(file)
        except (OSError, json.JSONDecodeError) as error:
            raise ArtifactError(f"'{path}' is not valid JSON: {error}") from error
        schema = record.get("schema_version")
        if schema not in SUPPORTED_TRIAL_SCHEMA_VERSIONS:
            raise ArtifactError(
                f"'{path}' has schema version {schema!r};"
                f" this analysis reads versions {', '.join(str(v) for v in SUPPORTED_TRIAL_SCHEMA_VERSIONS)}."
            )
        if record.get("status") not in ("completed", "failed"):
            raise ArtifactError(f"'{path}' has status {record.get('status')!r}.")
        run.trials.append(Trial(record=record, run_id=run.run_id, run_dir=run.run_dir))

    # Trials that the runner recorded but that produced no client record, such as
    # timeouts and crashes, still appear in the summary.
    for entry in manifest.get("trials", []):
        if entry.get("status") in ("timeout", "crashed", "cancelled") and entry.get("record") is None:
            run.trials.append(
                Trial(
                    record=_runner_failure_record(entry, run),
                    run_id=run.run_id,
                    run_dir=run.run_dir,
                )
            )
    return run


def _runner_failure_record(entry: dict[str, Any], run: Run) -> dict[str, Any]:
    return {
        "schema_version": 1,
        "implementation": entry.get("implementation", "unknown"),
        "implementation_version": None,
        "platform": entry.get("platform", "unknown"),
        "benchmark": entry.get("benchmark", "unknown"),
        "case": entry.get("case", "unknown"),
        "parameters": None,
        "latency_metric": "unknown",
        "operation_unit": "unknown",
        "load_shape": "closed_loop",
        "target": run.manifest.get("target", "unknown"),
        "injected_delay_ms": run.manifest.get("inject_delay_ms", 0),
        "status": "failed",
        "error": f"The runner stopped the trial: {entry.get('status')}. See {entry.get('stderr', 'the run directory')}.",
        "started_at_epoch_ms": entry.get("started_at_epoch_ms", 0),
        "connection_ready_ns": None,
        "elapsed_ns": 0,
        "counts": {
            "operations": 0,
            "request_messages": None,
            "response_messages": None,
            "application_request_bytes": 0,
            "application_response_bytes": 0,
        },
        "latency_samples_ns": [],
        "events": {},
    }


def group_trials(trials: list[Trial]) -> dict[tuple[str, str], list[Trial]]:
    """Groups trials by benchmark and case, sorted by benchmark and case name."""
    groups: dict[tuple[str, str], list[Trial]] = {}
    for trial in trials:
        groups.setdefault(trial.key(), []).append(trial)
    return dict(sorted(groups.items()))


def implementation_counts(trials: list[Trial]) -> dict[str, dict[str, int]]:
    """Per implementation: completed and failed trial counts."""
    counts: dict[str, dict[str, int]] = {}
    for trial in trials:
        entry = counts.setdefault(trial.implementation, {"completed": 0, "failed": 0})
        entry["completed" if trial.status == "completed" else "failed"] += 1
    return dict(sorted(counts.items()))


def trial_metrics(trial: Trial) -> dict[str, float | None]:
    """The per-trial aggregate metrics that the comparisons treat as one observation each.

    Latency percentiles come from the raw samples of the trial, so the trial stays
    the experimental unit and the analysis never pools distributions across trials.
    """
    metrics: dict[str, float | None] = {}
    samples = trial.samples()
    if samples:
        ordered = sorted(samples)
        for percentile in ("p50", "p90", "p95", "p99"):
            required = MIN_SAMPLES_FOR_PERCENTILE[percentile]
            if len(ordered) >= required:
                metrics[f"latency_{percentile}_us"] = _percentile_us(ordered, percentile)
            else:
                metrics[f"latency_{percentile}_us"] = None
        metrics["latency_min_us"] = ordered[0] / 1000.0
        metrics["latency_max_us"] = ordered[-1] / 1000.0
        metrics["latency_mean_us"] = sum(ordered) / len(ordered) / 1000.0
    else:
        for name in ("latency_p50_us", "latency_p90_us", "latency_p95_us", "latency_p99_us",
                     "latency_min_us", "latency_max_us", "latency_mean_us"):
            metrics[name] = None

    elapsed_s = trial.elapsed_ns / 1_000_000_000.0
    if trial.status == "completed" and elapsed_s > 0:
        if trial.operation_unit == "rpc":
            metrics["operations_per_second"] = trial.counts("operations") / elapsed_s
        request_messages = trial.counts("request_messages")
        response_messages = trial.counts("response_messages")
        if request_messages is not None:
            metrics["request_messages_per_second"] = request_messages / elapsed_s
        if response_messages is not None:
            metrics["response_messages_per_second"] = response_messages / elapsed_s
        metrics["application_request_mib_per_second"] = (
            trial.counts("application_request_bytes") / elapsed_s / (1024 * 1024)
        )
        metrics["application_response_mib_per_second"] = (
            trial.counts("application_response_bytes") / elapsed_s / (1024 * 1024)
        )
        metrics["elapsed_seconds"] = elapsed_s
    else:
        for name in ("operations_per_second", "request_messages_per_second", "response_messages_per_second",
                     "application_request_mib_per_second", "application_response_mib_per_second", "elapsed_seconds"):
            metrics[name] = None

    for event in ("time_to_first_response_ns", "final_response_latency_ns", "stream_duration_ns"):
        values = trial.events(event)
        metrics[f"{event}_us"] = median(values) / 1000.0 if values else None

    # CPU efficiency of the measured phase. A platform that can't read its process
    # counters reports no CPU values, and these metrics stay unset.
    cpu_total_ns = trial.cpu_total_ns()
    operations = trial.counts("operations") or 0
    if cpu_total_ns is not None and trial.status == "completed":
        if operations:
            metrics["cpu_per_operation_us"] = cpu_total_ns / 1000.0 / operations
        total_mib = (trial.counts("application_request_bytes") or 0) + (trial.counts("application_response_bytes") or 0)
        if total_mib > 0:
            metrics["cpu_per_mib_ms"] = cpu_total_ns / 1_000_000.0 / (total_mib / (1024 * 1024))
        if elapsed_s:
            metrics["cpu_to_wall_ratio"] = cpu_total_ns / 1_000_000_000.0 / elapsed_s
    for name in ("cpu_per_operation_us", "cpu_per_mib_ms", "cpu_to_wall_ratio"):
        metrics.setdefault(name, None)

    return metrics


def _percentile_us(sorted_samples: list[int], percentile: str) -> float:
    # The clients round the percentile index with ties away from zero, so this
    # uses the same rule. Python's `round` would round ties to the even index.
    fraction = {"p50": 0.50, "p90": 0.90, "p95": 0.95, "p99": 0.99}[percentile]
    index = int(fraction * (len(sorted_samples) - 1) + 0.5)
    return sorted_samples[index] / 1000.0


def percentile_sample_warnings(trials: list[Trial]) -> dict[str, int]:
    """Counts the trials whose sample count can't support each tail percentile."""
    warnings = {}
    for name, required in MIN_SAMPLES_FOR_PERCENTILE.items():
        short = sum(1 for trial in trials if trial.status == "completed" and len(trial.samples()) < required)
        if short:
            warnings[name] = short
    return warnings


def format_duration(seconds: float) -> str:
    if not math.isfinite(seconds):
        return "-"
    if seconds >= 120:
        return f"{seconds / 60:.0f} min"
    if seconds >= 10:
        return f"{seconds:.0f} s"
    return f"{seconds:.1f} s"
