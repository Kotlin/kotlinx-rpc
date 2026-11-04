# Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

"""Tests of artifact loading, metric extraction, and comparison pairing."""

from __future__ import annotations

import contextlib
import io
import json
import os
import sys
import tempfile
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[2]))

from analysis import artifacts, report


def trial_record(
    implementation="current",
    benchmark="unary-latency",
    case="default",
    status="completed",
    samples=(100, 200, 300),
    elapsed_ns=1_000_000_000,
    operations=100,
    request_messages=None,
    response_messages=None,
    platform="ios-simulator-arm64",
    target="localhost:50051",
    injected_delay_ms=0,
    parameters=None,
    error=None,
    events=None,
    operation_unit="rpc",
    latency_metric="call_round_trip",
):
    return {
        "schema_version": 1,
        "implementation": implementation,
        "implementation_version": None,
        "platform": platform,
        "benchmark": benchmark,
        "case": case,
        "parameters": parameters
        or {
            "warmup_operations": 10,
            "operations": operations,
            "concurrency": 1,
            "request_bytes": 0,
            "response_bytes": 0,
        },
        "latency_metric": latency_metric,
        "operation_unit": operation_unit,
        "load_shape": "closed_loop",
        "target": target,
        "injected_delay_ms": injected_delay_ms,
        "status": status,
        "error": error,
        "started_at_epoch_ms": 0,
        "connection_ready_ns": 1000,
        "elapsed_ns": elapsed_ns,
        "counts": {
            "operations": operations,
            "request_messages": request_messages,
            "response_messages": response_messages,
            "application_request_bytes": 0,
            "application_response_bytes": 1024 * operations,
        },
        "latency_samples_ns": list(samples),
        "events": events or {},
    }


def write_run(directory, run_id, records, manifest_trials=None, target="localhost:50051", seed=1):
    run_dir = Path(directory) / f"run-{run_id}"
    trials_dir = run_dir / "trials"
    trials_dir.mkdir(parents=True)
    for index, record in enumerate(records):
        name = f"{index:04d}-{record['implementation']}-{record['benchmark']}-{record['case']}.json"
        with open(trials_dir / name, "w", encoding="utf-8") as file:
            json.dump(record, file)
    manifest = {
        "schema_version": 1,
        "run_id": f"run-{run_id}",
        "repetitions": 1,
        "order": "random",
        "seed": seed,
        "platform": "ios-simulator-arm64",
        "target": target,
        "implementations": sorted({record["implementation"] for record in records}),
        "benchmark": records[0]["benchmark"] if records else "unknown",
        "case": None,
        "trials": manifest_trials or [],
    }
    with open(run_dir / "manifest.json", "w", encoding="utf-8") as file:
        json.dump(manifest, file)
    return run_dir


class LoadRunTest(unittest.TestCase):
    def setUp(self):
        self.temporary = tempfile.TemporaryDirectory()
        self.addCleanup(self.temporary.cleanup)

    def testLoadsTrialsAndGroupsByWorkload(self):
        records = [
            trial_record(implementation="current"),
            trial_record(implementation="swift"),
            trial_record(implementation="current", benchmark="unary-throughput"),
        ]
        run = artifacts.load_run(str(write_run(self.temporary.name, "a", records)))

        self.assertEqual(3, len(run.trials))
        groups = artifacts.group_trials(run.trials)
        self.assertEqual(2, len(groups))
        self.assertEqual({"current", "swift"}, set(run.implementations))

    def testMissingManifestRaises(self):
        with self.assertRaises(artifacts.ArtifactError):
            artifacts.load_run(self.temporary.name)

    def testRunnerFailuresAppearAsFailedTrials(self):
        manifest_trials = [
            {
                "index": 0,
                "implementation": "swift",
                "benchmark": "unary-latency",
                "case": "default",
                "record": None,
                "status": "timeout",
                "stderr": "trials/x.stderr.log",
            }
        ]
        run = artifacts.load_run(str(write_run(self.temporary.name, "b", [], manifest_trials)))

        self.assertEqual(1, len(run.trials))
        self.assertEqual("failed", run.trials[0].status)
        self.assertIn("timeout", run.trials[0].error)


class TrialMetricsTest(unittest.TestCase):
    def testComputesLatencyAndThroughputPerTrial(self):
        # Samples are nanoseconds: 200 samples from 100.0 us to 100.199 us.
        trial = artifacts.Trial(trial_record(samples=tuple(range(100_000, 100_200))), "run", "dir")
        metrics = artifacts.trial_metrics(trial)

        self.assertAlmostEqual(100.1, metrics["latency_p50_us"], delta=0.01)
        self.assertAlmostEqual(100.197, metrics["latency_p99_us"], delta=0.01)
        self.assertAlmostEqual(100.0, metrics["latency_min_us"], delta=0.01)
        self.assertAlmostEqual(100.0, metrics["operations_per_second"], delta=0.01)

    def testTailPercentileNeedsEnoughSamples(self):
        trial = artifacts.Trial(trial_record(samples=(10, 20)), "run", "dir")
        metrics = artifacts.trial_metrics(trial)

        # p50 needs two samples and is reported. The p90, p95, and p99 tails
        # need more support than two samples, so they stay unset.
        # Ties away from zero: the p50 of two samples is the larger one.
        self.assertAlmostEqual(0.02, metrics["latency_p50_us"], delta=0.001)
        self.assertIsNone(metrics["latency_p90_us"])
        self.assertIsNone(metrics["latency_p99_us"])
        self.assertEqual(0.01, metrics["latency_min_us"])

    def testStreamingMetricsSplitDirections(self):
        trial = artifacts.Trial(
            trial_record(
                benchmark="bidi-ping-pong",
                operation_unit="message",
                latency_metric="ping_pong_round_trip",
                samples=(1000, 2000),
                request_messages=10,
                response_messages=10,
                operations=20,
                elapsed_ns=2_000_000_000,
                events={"time_to_first_response_ns": [5000], "stream_duration_ns": [2_000_000_000]},
            ),
            "run",
            "dir",
        )
        metrics = artifacts.trial_metrics(trial)

        self.assertEqual(5.0, metrics["request_messages_per_second"])
        self.assertEqual(5.0, metrics["response_messages_per_second"])
        self.assertEqual(5.0, metrics["time_to_first_response_ns_us"])

    def testFailedTrialsHaveNoRatesButKeepSamples(self):
        trial = artifacts.Trial(trial_record(status="failed", error="boom"), "run", "dir")
        metrics = artifacts.trial_metrics(trial)

        # A failed trial keeps the samples it recorded before the failure, but
        # the analysis never pairs it, so its rates stay unset.
        self.assertIsNone(metrics["operations_per_second"])
        self.assertIsNotNone(metrics["latency_p50_us"])

    def testComparableKeyExcludesFailedAndMismatches(self):
        completed = artifacts.Trial(trial_record(), "run", "dir")
        failed = artifacts.Trial(trial_record(status="failed", error="x"), "run", "dir")
        other_platform = artifacts.Trial(trial_record(platform="macos"), "run", "dir")
        injected = artifacts.Trial(trial_record(injected_delay_ms=5), "run", "dir")

        self.assertIsNotNone(completed.comparable_key())
        self.assertIsNone(failed.comparable_key())
        self.assertNotEqual(completed.comparable_key(), other_platform.comparable_key())
        self.assertNotEqual(completed.comparable_key(), injected.comparable_key())


class CompareRunsTest(unittest.TestCase):
    def setUp(self):
        self.temporary = tempfile.TemporaryDirectory()
        self.addCleanup(self.temporary.cleanup)

    def testPairsByWorkloadAndReportsExclusions(self):
        baseline = [
            trial_record(implementation="current"),
            trial_record(implementation="current", benchmark="unary-throughput", case="only-baseline"),
        ]
        candidate = [
            trial_record(implementation="swift"),
            trial_record(implementation="swift", status="failed", error="boom"),
        ]
        baseline_dir = write_run(self.temporary.name, "base", baseline)
        candidate_dir = write_run(self.temporary.name, "cand", candidate)

        rows, notes = report.compare_runs(
            [artifacts.load_run(str(baseline_dir))],
            [artifacts.load_run(str(candidate_dir))],
            "current",
            "swift",
        )

        self.assertTrue(rows)
        self.assertIn("unary-latency", {row.benchmark for row in rows})
        self.assertTrue(any("only-baseline" in note for note in notes))
        self.assertTrue(any("excluded" in note for note in notes))

    def testMismatchedParametersAreNotCompared(self):
        baseline = [trial_record(implementation="current")]
        candidate = [
            trial_record(
                implementation="swift",
                parameters={
                    "warmup_operations": 999,
                    "operations": 100,
                    "concurrency": 1,
                    "request_bytes": 0,
                    "response_bytes": 0,
                },
            )
        ]
        baseline_dir = write_run(self.temporary.name, "base", baseline)
        candidate_dir = write_run(self.temporary.name, "cand", candidate)

        rows, notes = report.compare_runs(
            [artifacts.load_run(str(baseline_dir))],
            [artifacts.load_run(str(candidate_dir))],
            "current",
            "swift",
        )

        self.assertEqual([], rows)
        self.assertTrue(notes)

    def testComparisonTableReportsVerdicts(self):
        baseline = [
            trial_record(implementation="current", samples=(100,) * 50, elapsed_ns=1_000_000_000)
            for _ in range(6)
        ]
        candidate = [
            trial_record(implementation="swift", samples=(200,) * 50, elapsed_ns=2_000_000_000)
            for _ in range(6)
        ]
        baseline_dir = write_run(self.temporary.name, "base", baseline)
        candidate_dir = write_run(self.temporary.name, "cand", candidate)

        rows, notes = report.compare_runs(
            [artifacts.load_run(str(baseline_dir))],
            [artifacts.load_run(str(candidate_dir))],
            "current",
            "swift",
        )
        table = report.comparison_table(rows, notes, "base:current", "cand:swift")

        self.assertIn("latency p50 (us)", table)
        self.assertIn("difference detected", table)
        self.assertIn("higher is better", table)


class EquivalenceTest(unittest.TestCase):
    def testEquivalenceOfIdenticalMatrices(self):
        from analysis import cli as analysis_cli

        matrix = [
            {
                "benchmark": "unary-latency",
                "description": "one",
                "latency_metric": "call_round_trip",
                "operation_unit": "rpc",
                "cases": [
                    {
                        "case": "default",
                        "parameters": {
                            "warmup_operations": 200,
                            "operations": 10_000,
                            "concurrency": 1,
                            "request_bytes": 0,
                            "response_bytes": 0,
                        },
                    }
                ],
            }
        ]
        with tempfile.TemporaryDirectory() as directory:
            paths = []
            for name in ("a", "b"):
                path = os.path.join(directory, f"{name}.json")
                with open(path, "w", encoding="utf-8") as file:
                    json.dump(matrix, file)
                paths.append(path)

            output = io.StringIO()
            with contextlib.redirect_stdout(output):
                code = analysis_cli.main(["equivalence", *paths])

        self.assertEqual(0, code)
        self.assertIn("agree", output.getvalue())

    def testEquivalenceDetectsParameterDrift(self):
        from analysis import cli as analysis_cli

        matrix = [
            {
                "benchmark": "unary-latency",
                "description": "d",
                "latency_metric": "call_round_trip",
                "operation_unit": "rpc",
                "cases": [
                    {
                        "case": "default",
                        "parameters": {
                            "warmup_operations": 200,
                            "operations": 10_000,
                            "concurrency": 1,
                            "request_bytes": 0,
                            "response_bytes": 0,
                        },
                    }
                ],
            }
        ]
        drifted = json.loads(json.dumps(matrix))
        drifted[0]["cases"][0]["parameters"]["operations"] = 999

        with tempfile.TemporaryDirectory() as directory:
            paths = []
            for name, content in (("a", matrix), ("b", drifted)):
                path = os.path.join(directory, f"{name}.json")
                with open(path, "w", encoding="utf-8") as file:
                    json.dump(content, file)
                paths.append(path)

            stderr = io.StringIO()
            with contextlib.redirect_stderr(stderr):
                code = analysis_cli.main(["equivalence", *paths])

        self.assertEqual(1, code)
        self.assertIn("parameters", stderr.getvalue())


if __name__ == "__main__":
    unittest.main()
