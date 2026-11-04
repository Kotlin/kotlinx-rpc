# Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

"""Tests of the HTML report structure."""

from __future__ import annotations

import sys
import tempfile
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[2]))

from analysis import artifacts, html_report, report
from analysis.tests.test_artifacts import trial_record, write_run


class HtmlReportTest(unittest.TestCase):
    def setUp(self):
        self.temporary = tempfile.TemporaryDirectory()
        self.addCleanup(self.temporary.cleanup)

    def testAnalyzeHtmlContainsChartsAndTables(self):
        records = [
            trial_record(implementation="current"),
            trial_record(implementation="swift", samples=(200, 300, 400)),
        ]
        run_dir = write_run(self.temporary.name, "html", records)
        run = artifacts.load_run(str(run_dir))

        page = html_report.analyze_html([run])

        self.assertIn("<!DOCTYPE html>", page)
        self.assertIn("unary-latency / default", page)
        self.assertIn("<svg", page)
        self.assertIn("<table>", page)
        self.assertIn("latency p50 (us)", page)
        # Both series colors appear once per implementation.
        self.assertIn("#2a78d6", page)
        self.assertIn("#eb6834", page)

    def testComparisonHtmlStatesVerdictAndInterval(self):
        baseline = [
            trial_record(implementation="current", samples=(100_000,) * 50) for _ in range(5)
        ]
        candidate = [
            trial_record(implementation="swift", samples=(200_000,) * 50) for _ in range(5)
        ]
        baseline_dir = write_run(self.temporary.name, "base", baseline)
        candidate_dir = write_run(self.temporary.name, "cand", candidate)
        rows, notes = report.compare_runs(
            [artifacts.load_run(str(baseline_dir))],
            [artifacts.load_run(str(candidate_dir))],
            "current",
            "swift",
        )

        page = html_report.comparison_html(rows, notes, "base:current", "cand:swift")

        self.assertIn("difference detected", page)
        self.assertIn("bootstrap", page)
        self.assertIn("lower is better", page)
        if notes:
            self.assertIn("<h2>Exclusions</h2>", page)

    def testHtmlEscapesBenchmarkNames(self):
        records = [trial_record(benchmark="weird<&>name")]
        run_dir = write_run(self.temporary.name, "escape", records)
        run = artifacts.load_run(str(run_dir))

        page = html_report.analyze_html([run])

        self.assertIn("weird&lt;&amp;&gt;name", page)
        self.assertNotIn("weird<&>name", page)


if __name__ == "__main__":
    unittest.main()
