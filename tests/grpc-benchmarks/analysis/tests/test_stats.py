# Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

"""Tests of the comparison statistics. The trial is the experimental unit."""

from __future__ import annotations

import sys
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[2]))

from analysis import stats


class CompareMetricTest(unittest.TestCase):
    def testDetectsAReliableDifference(self):
        baseline = [100.0, 101.0, 99.0, 100.5, 100.2, 99.8, 100.1, 100.0, 99.9, 100.3]
        candidate = [200.0, 201.0, 199.0, 200.5, 200.2, 199.8, 200.1, 200.0, 199.9, 200.3]

        comparison = stats.compare_metric("latency_p50_us", baseline, candidate)

        self.assertTrue(comparison.difference_detected)
        self.assertGreater(comparison.ratio, 1.9)
        self.assertLess(comparison.ratio, 2.1)
        self.assertGreater(comparison.ratio_interval[0], 1.5)
        self.assertLess(comparison.ratio_interval[1], 2.5)
        # The p-value uses the normal approximation of the U test, so its
        # accuracy is limited at ten trials per side. The bootstrap interval is
        # the primary decision rule.
        self.assertIsNotNone(comparison.p_value)
        self.assertLess(comparison.p_value, 0.01)
        self.assertEqual("high", comparison.confidence)

    def testIdenticalDistributionsStayInconclusive(self):
        values = [100.0, 101.0, 99.0, 100.5, 100.2, 99.8, 100.1, 100.0, 99.9, 100.3]

        comparison = stats.compare_metric("latency_p50_us", values, list(values))

        self.assertFalse(comparison.difference_detected)
        self.assertEqual("no difference detected", comparison.verdict)

    def testFewTrialsReportInsufficient(self):
        comparison = stats.compare_metric("latency_p50_us", [100.0, 101.0], [200.0, 201.0])

        self.assertIsNone(comparison.difference_detected)
        self.assertEqual("insufficient trials", comparison.confidence)

    def testThreeVersusThreeTrialsGiveAnIntervalWithLowConfidence(self):
        comparison = stats.compare_metric(
            "latency_p50_us",
            [100.0, 100.0, 100.0],
            [150.0, 150.0, 150.0],
        )

        self.assertTrue(comparison.difference_detected)
        self.assertEqual("low", comparison.confidence)

    def testEmptyGroupsReportInsufficient(self):
        comparison = stats.compare_metric("latency_p50_us", [], [1.0, 2.0])

        self.assertIsNone(comparison.ratio)
        self.assertEqual("insufficient trials", comparison.confidence)

    def testBootstrapIsSeededAndReproducible(self):
        baseline = [10.0, 11.0, 9.0, 10.5, 12.0, 10.1, 9.9, 10.2]
        candidate = [20.0, 21.0, 19.0, 20.5, 22.0, 20.1, 19.9, 20.2]

        first = stats.compare_metric("latency_p50_us", baseline, candidate)
        second = stats.compare_metric("latency_p50_us", baseline, candidate)

        self.assertEqual(first.ratio_interval, second.ratio_interval)

    def testIgnoredValuesAreDropped(self):
        comparison = stats.compare_metric(
            "latency_p50_us",
            [100.0, None, 101.0, float("nan"), 100.0, 100.1, 100.2, 100.0, 100.4, 100.1],
            [100.0, 100.2, 99.9, 100.1, 100.0, 100.3, 100.0, 100.1, 99.8, 100.2],
        )

        self.assertEqual(8, len(comparison.baseline))
        self.assertEqual(10, len(comparison.candidate))


class MannWhitneyTest(unittest.TestCase):
    def testAllValuesSeparatedGiveASmallPValue(self):
        baseline = [float(value) for value in range(10)]
        candidate = [float(value) for value in range(20, 30)]

        p_value = stats._mann_whitney_u(baseline, candidate)

        # The normal approximation is coarse at ten trials per side.
        self.assertLess(p_value, 0.01)

    def testIdenticalSamplesGiveLargePValue(self):
        values = [float(value) for value in range(10)]

        p_value = stats._mann_whitney_u(values, list(values))

        # The tie correction narrows the approximated variance, so identical
        # samples land slightly below 0.5 instead of exactly at it.
        self.assertGreater(p_value, 0.4)

    def testTiesDoNotCrash(self):
        baseline = [1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0]
        candidate = [1.0, 1.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0]

        p_value = stats._mann_whitney_u(baseline, candidate)

        self.assertGreaterEqual(p_value, 0.0)
        self.assertLessEqual(p_value, 1.0)


class MedianTest(unittest.TestCase):
    def testOddAndEvenCounts(self):
        self.assertEqual(3.0, stats.median([1.0, 3.0, 5.0]))
        self.assertEqual(4.0, stats.median([1.0, 3.0, 5.0, 7.0]))


if __name__ == "__main__":
    unittest.main()
