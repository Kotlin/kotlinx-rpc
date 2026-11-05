"""Terminal and CSV reports for benchmark runs and comparisons."""

from __future__ import annotations

import csv
import io
import math
from statistics import median
from dataclasses import dataclass
from itertools import groupby

from tabulate import tabulate

from .artifacts import (
    Run,
    Trial,
    format_duration,
    group_trials,
    implementation_counts,
    percentile_sample_warnings,
    trial_metrics,
)
from .stats import Comparison, compare_metric

# Metrics that a lower value improves.
LATENCY_METRICS = {
    "cpu_per_operation_us",
    "cpu_per_mib_ms",
    "cpu_to_wall_ratio",
    "latency_p50_us",
    "latency_p90_us",
    "latency_p95_us",
    "latency_p99_us",
    "latency_min_us",
    "latency_max_us",
    "latency_mean_us",
    "time_to_first_response_ns_us",
    "final_response_latency_ns_us",
    "stream_duration_ns_us",
    "elapsed_seconds",
}

# Metrics that a higher value improves.
THROUGHPUT_METRICS = {
    "operations_per_second",
    "request_messages_per_second",
    "response_messages_per_second",
    "application_request_mib_per_second",
    "application_response_mib_per_second",
}

SUMMARY_METRICS = [
    "cpu_per_operation_us",
    "cpu_per_mib_ms",
    "cpu_to_wall_ratio",
    "latency_p50_us",
    "latency_p99_us",
    "operations_per_second",
    "request_messages_per_second",
    "response_messages_per_second",
    "application_request_mib_per_second",
    "application_response_mib_per_second",
    "time_to_first_response_ns_us",
    "final_response_latency_ns_us",
    "elapsed_seconds",
]

COMPARISON_METRICS = [
    "cpu_per_operation_us",
    "cpu_per_mib_ms",
    "cpu_to_wall_ratio",
    "latency_p50_us",
    "latency_p90_us",
    "latency_p99_us",
    "operations_per_second",
    "request_messages_per_second",
    "response_messages_per_second",
    "application_request_mib_per_second",
    "application_response_mib_per_second",
    "time_to_first_response_ns_us",
    "final_response_latency_ns_us",
]

METRIC_LABELS = {
    "cpu_per_operation_us": "CPU per operation (us)",
    "cpu_per_mib_ms": "CPU per MiB (ms)",
    "cpu_to_wall_ratio": "CPU to wall ratio",
    "latency_p50_us": "latency p50 (us)",
    "latency_p90_us": "latency p90 (us)",
    "latency_p95_us": "latency p95 (us)",
    "latency_p99_us": "latency p99 (us)",
    "latency_min_us": "latency min (us)",
    "latency_max_us": "latency max (us)",
    "latency_mean_us": "latency mean (us)",
    "operations_per_second": "operations/s",
    "request_messages_per_second": "request messages/s",
    "response_messages_per_second": "response messages/s",
    "application_request_mib_per_second": "request data (MiB/s)",
    "application_response_mib_per_second": "response data (MiB/s)",
    "time_to_first_response_ns_us": "time to first response (us)",
    "final_response_latency_ns_us": "final response latency (us)",
    "stream_duration_ns_us": "stream duration (us)",
    "elapsed_seconds": "elapsed (s)",
}

METRIC_UNITS = {
    "cpu_per_operation_us": "us",
    "cpu_per_mib_ms": "ms",
    "cpu_to_wall_ratio": "ratio",
    "latency_p50_us": "us",
    "latency_p90_us": "us",
    "latency_p99_us": "us",
    "operations_per_second": "op/s",
    "request_messages_per_second": "msg/s",
    "response_messages_per_second": "msg/s",
    "application_request_mib_per_second": "MiB/s",
    "application_response_mib_per_second": "MiB/s",
    "time_to_first_response_ns_us": "us",
    "final_response_latency_ns_us": "us",
    "elapsed_seconds": "s",
}


@dataclass
class ComparisonRow:
    benchmark: str
    case: str
    metric: str
    label: str
    unit: str
    lower_is_better: bool
    comparison: Comparison


def metric_is_lower_better(metric: str) -> bool:
    return metric in LATENCY_METRICS


def format_value(value: float | None, metric: str) -> str:
    if value is None or not math.isfinite(value):
        return "-"
    if metric == "cpu_per_mib_ms":
        if value >= 1_000:
            return f"{value / 1_000:.1f} s"
        return f"{value:.2f}"
    if metric == "cpu_to_wall_ratio":
        return f"{value:.2f}"
    if metric.endswith("_us"):
        if value >= 10_000:
            return f"{value / 1_000:.1f} ms"
        return f"{value:.1f}"
    if metric.endswith("_per_second"):
        if value >= 1_000_000:
            return f"{value / 1_000_000:.2f}M"
        if value >= 1_000:
            return f"{value / 1_000:.1f}k"
        return f"{value:.1f}"
    if metric == "elapsed_seconds":
        return format_duration(value)
    return f"{value:.3f}"


def format_ratio(ratio: float | None) -> str:
    if ratio is None or not math.isfinite(ratio):
        return "-"
    if ratio >= 10:
        return f"{ratio:.1f}x"
    return f"{ratio:.2f}x"


def summarize_run(run: Run) -> str:
    """The human-readable summary of one run directory."""
    lines: list[str] = []
    lines.append(f"run: {run.run_id} ({run.run_dir})")
    environment = run.manifest.get("environment", {})
    git = environment.get("git", {})
    lines.append(
        f"revision: {git.get('revision', 'unknown')}{' (dirty)' if git.get('dirty') else ''}"
        f", platform: {run.manifest.get('platform', 'unknown')}"
        f", target: {run.manifest.get('target', 'unknown')}"
        f", order: {run.manifest.get('order', 'unknown')}"
        f", seed: {run.manifest.get('seed', '-')}"
        f", repetitions: {run.manifest.get('repetitions', '-')}"
    )

    if not run.trials:
        lines.append("no trial records found")
        return "\n".join(lines)

    counts = implementation_counts(run.trials)
    summary = ", ".join(
        f"{implementation}: {entry['completed']} completed, {entry['failed']} failed"
        for implementation, entry in counts.items()
    )
    lines.append(f"trials: {summary}")
    lines.append("")

    for (benchmark, case), trials in group_trials(run.trials).items():
        lines.append(f"{benchmark} / {case}")
        for implementation in sorted({trial.implementation for trial in trials}):
            implementation_trials = [trial for trial in trials if trial.implementation == implementation]
            completed = [trial for trial in implementation_trials if trial.status == "completed"]
            failed = [trial for trial in implementation_trials if trial.status == "failed"]
            for trial in failed:
                lines.append(f"  {implementation}: FAILED: {trial.error}")
            if not completed:
                continue
            lines.append(f"  {implementation} ({len(completed)} completed trials)")
            metrics = [trial_metrics(trial) for trial in completed]
            table = []
            for metric in SUMMARY_METRICS:
                values = [m[metric] for m in metrics if m.get(metric) is not None]
                if values:
                    table.append([
                        METRIC_LABELS.get(metric, metric),
                        format_value(median(values), metric),
                        format_value(min(values), metric),
                        format_value(max(values), metric),
                    ])
            lines.append(tabulate(table, headers=["metric", "median", "min", "max"], disable_numparse=True))
            warnings = percentile_sample_warnings(completed)
            if warnings:
                flagged = ", ".join(f"{name} in {count} trials" for name, count in warnings.items())
                lines.append(f"    tail percentile warning: too few samples for {flagged}")
        lines.append("")

    return "\n".join(lines)


def run_summary_csv(runs: list[Run]) -> str:
    """One row per (run, benchmark, case, implementation, metric)."""
    output = io.StringIO()
    writer = csv.writer(output)
    writer.writerow([
        "run_id", "implementation", "benchmark", "case", "completed_trials", "failed_trials",
        "metric", "median", "min", "max",
    ])
    for run in runs:
        for (benchmark, case), trials in group_trials(run.trials).items():
            by_implementation: dict[str, list[Trial]] = {}
            for trial in trials:
                by_implementation.setdefault(trial.implementation, []).append(trial)
            for implementation, implementation_trials in sorted(by_implementation.items()):
                completed = [trial for trial in implementation_trials if trial.status == "completed"]
                failed = len(implementation_trials) - len(completed)
                metrics = [trial_metrics(trial) for trial in completed]
                for metric in SUMMARY_METRICS:
                    values = [m[metric] for m in metrics if m.get(metric) is not None]
                    if not values:
                        continue
                    writer.writerow([
                        run.run_id, implementation, benchmark, case, len(completed), failed,
                        metric, _csv_number(median(values)), _csv_number(min(values)), _csv_number(max(values)),
                    ])
    return output.getvalue()


def _csv_number(value: float) -> str:
    return f"{value:.6f}"


def compare_runs(baseline_runs: list[Run], candidate_runs: list[Run],
                 baseline_implementation: str, candidate_implementation: str,
                 tolerate_injected_delay: bool = False) -> tuple[list[ComparisonRow], list[str]]:
    """Pairs the trials of two implementations by workload identity.

    Returns the comparisons and the exclusion notes for trials that couldn't
    be paired. `tolerate_injected_delay` pairs runs whose injected delay
    differs, for the positive-control workflow.
    """
    baseline_trials = _select_trials(baseline_runs, baseline_implementation)
    candidate_trials = _select_trials(candidate_runs, candidate_implementation)
    notes: list[str] = []

    if tolerate_injected_delay:
        baseline_delays = sorted({trial.injected_delay_ms for trial in baseline_trials})
        candidate_delays = sorted({trial.injected_delay_ms for trial in candidate_trials})
        if baseline_delays != candidate_delays:
            notes.append(
                f"injected delay differs: baseline {baseline_delays} ms, candidate {candidate_delays} ms"
            )

    baseline_by_key: dict[tuple, list[Trial]] = {}
    for trial in baseline_trials:
        key = trial.comparable_key(tolerate_injected_delay)
        if key is None:
            notes.append(f"baseline {trial.implementation} {trial.benchmark}/{trial.case}: trial {trial.status}, excluded")
            continue
        baseline_by_key.setdefault(key, []).append(trial)
    for trial in candidate_trials:
        key = trial.comparable_key(tolerate_injected_delay)
        if key is None:
            notes.append(f"candidate {trial.implementation} {trial.benchmark}/{trial.case}: trial {trial.status}, excluded")

    rows: list[ComparisonRow] = []
    keys = sorted(set(baseline_by_key) | {
        trial.comparable_key(tolerate_injected_delay)
        for trial in candidate_trials
        if trial.comparable_key(tolerate_injected_delay)
    })
    for key in keys:
        if key not in baseline_by_key:
            notes.append(f"{key[0]}/{key[1]}: no completed baseline trial of '{baseline_implementation}', excluded")
            continue
        candidates = [
            trial for trial in candidate_trials
            if trial.comparable_key(tolerate_injected_delay) == key
        ]
        if not candidates:
            notes.append(f"{key[0]}/{key[1]}: no completed candidate trial of '{candidate_implementation}', excluded")
            continue

        benchmark = key[0]
        baseline_metrics = [trial_metrics(trial) for trial in baseline_by_key[key]]
        candidate_metrics = [trial_metrics(trial) for trial in candidates]
        for metric in COMPARISON_METRICS:
            baseline_values = [m[metric] for m in baseline_metrics if m.get(metric) is not None]
            candidate_values = [m[metric] for m in candidate_metrics if m.get(metric) is not None]
            if not baseline_values or not candidate_values:
                continue
            # A metric that is zero on both sides carries no signal, for
            # example the byte rates of an empty-payload case.
            if not any(baseline_values) and not any(candidate_values):
                continue
            comparison = compare_metric(metric, baseline_values, candidate_values)
            rows.append(
                ComparisonRow(
                    benchmark=benchmark,
                    case=key[1],
                    metric=metric,
                    label=METRIC_LABELS.get(metric, metric),
                    unit=METRIC_UNITS.get(metric, ""),
                    lower_is_better=metric_is_lower_better(metric),
                    comparison=comparison,
                )
            )
    return rows, notes


def _select_trials(runs: list[Run], implementation: str) -> list[Trial]:
    return [trial for run in runs for trial in run.trials if trial.implementation == implementation]


def comparison_table(rows: list[ComparisonRow], notes: list[str],
                     baseline_label: str, candidate_label: str) -> str:
    """The human-readable comparison report."""
    lines: list[str] = []
    lines.append(f"baseline: {baseline_label}")
    lines.append(f"candidate: {candidate_label}")
    lines.append("")
    for (benchmark, case), workload_rows in groupby(rows, key=lambda row: (row.benchmark, row.case)):
        lines.append(f"{benchmark} / {case}")
        table = []
        for row in workload_rows:
            comparison = row.comparison
            interval = (
                f"{format_ratio(comparison.ratio_interval[0])} .. {format_ratio(comparison.ratio_interval[1])}"
                if comparison.ratio_interval else "n/a"
            )
            table.append([
                row.label,
                format_value(comparison.baseline_median, row.metric),
                format_value(comparison.candidate_median, row.metric),
                format_ratio(comparison.ratio),
                interval,
                f"{comparison.p_value:.3f}" if comparison.p_value is not None else "n/a",
                comparison.verdict,
                comparison.confidence,
                "lower" if row.lower_is_better else "higher",
            ])
        lines.append(tabulate(
            table,
            headers=["metric", "baseline", "candidate", "ratio", "95% interval", "p", "verdict", "confidence", "better"],
            disable_numparse=True,
        ))
    detected = sum(1 for row in rows if row.comparison.difference_detected)
    inconclusive = sum(1 for row in rows if not row.comparison.difference_detected)
    lines.append("")
    lines.append(f"{len(rows)} metric comparisons: {detected} with a detected difference, {inconclusive} inconclusive")
    if notes:
        lines.append("")
        lines.append("exclusions:")
        for note in notes:
            lines.append(f"  {note}")
    return "\n".join(lines)


def comparison_csv(rows: list[ComparisonRow], baseline_label: str, candidate_label: str) -> str:
    output = io.StringIO()
    writer = csv.writer(output)
    writer.writerow([
        "benchmark", "case", "metric", "unit", "lower_is_better",
        "baseline_trials", "candidate_trials",
        "baseline_median", "candidate_median",
        "ratio", "ratio_interval_low", "ratio_interval_high",
        "mann_whitney_p", "difference_detected", "confidence",
    ])
    for row in rows:
        comparison = row.comparison
        writer.writerow([
            row.benchmark, row.case, row.metric, row.unit, row.lower_is_better,
            len(comparison.baseline), len(comparison.candidate),
            _csv_number(comparison.baseline_median) if comparison.baseline else "",
            _csv_number(comparison.candidate_median) if comparison.candidate else "",
            _csv_number(comparison.ratio) if comparison.ratio is not None else "",
            _csv_number(comparison.ratio_interval[0]) if comparison.ratio_interval else "",
            _csv_number(comparison.ratio_interval[1]) if comparison.ratio_interval else "",
            f"{comparison.p_value:.6f}" if comparison.p_value is not None else "",
            comparison.difference_detected if comparison.difference_detected is not None else "",
            comparison.confidence,
        ])
    return output.getvalue()
