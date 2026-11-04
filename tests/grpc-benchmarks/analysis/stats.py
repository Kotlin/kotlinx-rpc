"""Statistics for benchmark comparisons.

The experimental unit is one trial: one client process that ran one benchmark
case once. Latency percentiles and rates are computed per trial, and the
uncertainty of a comparison comes from the spread between trials. A single
trial never proves a difference, no matter how many latency samples it holds.

Sources of the method:

- Google Benchmark reports the mean, median, standard deviation, and
  coefficient of variation over repetitions, and its `compare.py` tool runs a
  Mann-Whitney U test, which needs at least nine repetitions to be meaningful.
- JMH treats run-to-run variance as the quantity to measure, and aggregates
  results only across separate process launches.

The implementation uses only the Python standard library.
"""

from __future__ import annotations

import math
import random
from dataclasses import dataclass

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


def median(values: list[float]) -> float:
    ordered = sorted(values)
    middle = len(ordered) // 2
    if len(ordered) % 2 == 1:
        return ordered[middle]
    return (ordered[middle - 1] + ordered[middle]) / 2.0


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

    total_trials = len(baseline) + len(candidate)
    if min(len(baseline), len(candidate)) < 3:
        # With fewer than three trials per side the interval is meaningless.
        return Comparison(
            metric, baseline, candidate, baseline_median, candidate_median,
            ratio, None, None, None, "insufficient trials",
        )

    interval = _bootstrap_ratio_interval(baseline, candidate)
    p_value = (
        _mann_whitney_u(baseline, candidate)
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
    """The 95th percentile interval of the ratio of medians over trial resamples."""
    generator = random.Random(BOOTSTRAP_SEED)
    ratios: list[float] = []
    for _ in range(BOOTSTRAP_RESAMPLES):
        resampled_baseline = _resample(baseline, generator)
        resampled_candidate = _resample(candidate, generator)
        baseline_median = median(resampled_baseline)
        candidate_median = median(resampled_candidate)
        if baseline_median == 0:
            continue
        ratios.append(candidate_median / baseline_median)
    ratios.sort()
    if not ratios:
        return (0.0, math.inf)
    lower = ratios[int(0.025 * (len(ratios) - 1))]
    upper = ratios[int(0.975 * (len(ratios) - 1))]
    return (lower, upper)


def _resample(values: list[float], generator: random.Random) -> list[float]:
    count = len(values)
    return [values[generator.randrange(count)] for _ in range(count)]


def _mann_whitney_u(baseline: list[float], candidate: list[float]) -> float:
    """Two-sided p-value of the Mann-Whitney U test with the normal approximation.

    The null hypothesis is that neither group tends to produce larger values.
    """
    combined = [(value, 0) for value in baseline] + [(value, 1) for value in candidate]
    combined.sort(key=lambda item: item[0])

    # Average ranks over ties.
    ranks: list[float] = []
    index = 0
    while index < len(combined):
        end = index
        while end + 1 < len(combined) and combined[end + 1][0] == combined[index][0]:
            end += 1
        average_rank = (index + end) / 2.0 + 1.0
        ranks.extend([average_rank] * (end - index + 1))
        index = end + 1

    baseline_rank_sum = sum(
        rank for rank, (_, group) in zip(ranks, combined) if group == 0
    )
    n_baseline = len(baseline)
    n_candidate = len(candidate)
    u_baseline = baseline_rank_sum - n_baseline * (n_baseline - 1) / 2.0
    u = min(u_baseline, n_baseline * n_candidate - u_baseline)

    # Normal approximation with the tie correction.
    total = n_baseline + n_candidate
    squared_rank_sum = 0.0
    index = 0
    while index < len(combined):
        end = index
        while end + 1 < len(combined) and combined[end + 1][0] == combined[index][0]:
            end += 1
        tie_count = end - index + 1
        squared_rank_sum += tie_count ** 3 - tie_count
        index = end + 1
    variance = (
        n_baseline * n_candidate / 12.0
        * ((total + 1) - squared_rank_sum / (total * (total - 1)))
    )
    mean_u = n_baseline * n_candidate / 2.0
    if variance <= 0:
        return 1.0
    z = (u - mean_u + 0.5) / math.sqrt(variance)
    return 2.0 * _normal_cdf(z)


def _normal_cdf(value: float) -> float:
    return 0.5 * (1.0 + math.erf(value / math.sqrt(2.0)))
