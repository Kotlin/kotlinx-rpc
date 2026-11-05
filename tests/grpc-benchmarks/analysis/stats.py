"""Compare independent benchmark trials with a bootstrap interval and Mann-Whitney U test."""

from __future__ import annotations

import math
from dataclasses import dataclass
from statistics import median

import numpy as np
from scipy.stats import bootstrap, mannwhitneyu

# Google Benchmark's compare.py documents this bound for the Mann-Whitney U test.
MIN_TRIALS_FOR_U_TEST = 8

# Below this count the bootstrap interval is so wide that the comparison
# reports low confidence even when the interval excludes the no-difference line.
MIN_TRIALS_FOR_CONFIDENT_VERDICT = 5

BOOTSTRAP_RESAMPLES = 10_000
BOOTSTRAP_SEED = 20260101


@dataclass
class Comparison:
    """One metric compared between two independent trial groups."""

    metric: str
    baseline: list[float]
    candidate: list[float]
    baseline_median: float
    candidate_median: float
    ratio: float | None
    ratio_interval: tuple[float, float] | None
    p_value: float | None
    difference_detected: bool | None
    confidence: str

    @property
    def verdict(self) -> str:
        if self.difference_detected is None:
            return "inconclusive"
        if self.difference_detected:
            return "difference detected"
        return "no difference detected"


def compare_metric(
    metric: str,
    baseline: list[float],
    candidate: list[float],
) -> Comparison:
    """Compares one metric between two groups of per-trial observations.

    The effect size is the ratio of the group medians. Its uncertainty is a
    percentile bootstrap interval over the trials. The p-value is the
    Mann-Whitney U test with the normal approximation, which needs at least
    [MIN_TRIALS_FOR_U_TEST] trials per side.

    For latency metrics a ratio below 1 means that the candidate is faster. For
    throughput metrics a ratio below 1 means that the candidate is slower. The
    caller states the direction in its output.
    """
    baseline = [value for value in baseline if value is not None and math.isfinite(value)]
    candidate = [value for value in candidate if value is not None and math.isfinite(value)]
    if not baseline or not candidate:
        return Comparison(metric, baseline, candidate, 0.0, 0.0, None, None, None, None, "insufficient trials")

    baseline_median = median(baseline)
    candidate_median = median(candidate)

    if baseline_median == 0 and candidate_median == 0:
        ratio = 1.0
    elif baseline_median == 0:
        ratio = math.inf if candidate_median > 0 else 0.0
    else:
        ratio = candidate_median / baseline_median

    if min(len(baseline), len(candidate)) < 3:
        # With fewer than three trials per side the interval is meaningless.
        return Comparison(
            metric, baseline, candidate, baseline_median, candidate_median,
            ratio, None, None, None, "insufficient trials",
        )

    interval = _bootstrap_ratio_interval(baseline, candidate)
    p_value = (
        float(mannwhitneyu(baseline, candidate, alternative="two-sided", method="asymptotic").pvalue)
        if min(len(baseline), len(candidate)) >= MIN_TRIALS_FOR_U_TEST
        else None
    )

    lower, upper = interval
    detected = (lower > 1.0 or upper < 1.0)
    confidence = "high" if min(len(baseline), len(candidate)) >= MIN_TRIALS_FOR_CONFIDENT_VERDICT else "low"

    return Comparison(
        metric,
        baseline,
        candidate,
        baseline_median,
        candidate_median,
        ratio,
        interval,
        p_value,
        detected,
        confidence,
    )


def _bootstrap_ratio_interval(baseline: list[float], candidate: list[float]) -> tuple[float, float]:
    """Return the 95% percentile interval of the ratio of independent trial medians."""
    def ratio(base, candidate, axis=-1):
        denominator = np.median(base, axis=axis)
        numerator = np.median(candidate, axis=axis)
        with np.errstate(divide="ignore", invalid="ignore"):
            return numerator / denominator

    result = bootstrap(
        (np.asarray(baseline), np.asarray(candidate)),
        ratio,
        paired=False,
        vectorized=True,
        n_resamples=BOOTSTRAP_RESAMPLES,
        method="percentile",
        rng=np.random.default_rng(BOOTSTRAP_SEED),
    )
    interval = result.confidence_interval
    if not np.isfinite([interval.low, interval.high]).all():
        return (0.0, math.inf)
    return (float(interval.low), float(interval.high))
