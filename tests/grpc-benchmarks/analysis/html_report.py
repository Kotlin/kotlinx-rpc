"""Offline HTML reports with interactive Plotly charts and per-trial summaries."""

from __future__ import annotations

import html
from statistics import median

import plotly.graph_objects as go
from plotly.offline import get_plotlyjs
from tabulate import tabulate

from .artifacts import Run, group_trials, implementation_counts, trial_metrics
from .report import (
    METRIC_LABELS,
    METRIC_UNITS,
    SUMMARY_METRICS,
    ComparisonRow,
)

SERIES_COLORS = ["#2a78d6", "#eb6834", "#1baf7a"]

IMPLEMENTATIONS = ["current", "legacy", "swift"]


def _color(index: int) -> str:
    return SERIES_COLORS[index % len(SERIES_COLORS)]


def _style() -> str:
    return """<style>
body { font-family: system-ui, sans-serif; background: #fcfcfb; color: #0b0b0b; }
.viz-root { max-width: 70rem; margin: auto; padding: 1.5rem; }
h2 { margin-top: 2rem; }
table { border-collapse: collapse; margin: 1rem 0; }
th, td { padding: .3rem .6rem; text-align: right; border-bottom: 1px solid #ddd; }
th:first-child, td:first-child { text-align: left; }
.legend .swatch { display: inline-block; width: 10px; height: 10px; margin: 0 .3rem 0 1rem; }
.failed { color: #b3261e; }
</style>"""


def _page(title: str, body: str) -> str:
    return f"""<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>{html.escape(title)}</title>
{_style()}
<script>{get_plotlyjs()}</script>
</head>
<body>
<div class="viz-root">
{body}
</div>
</body>
</html>
"""


def _dot_plot(rows: list[tuple[str, str, float | None, float | None, float | None]], title: str) -> str:
    """Plot each median with the minimum and maximum of its trial group."""
    figure = go.Figure()
    for label, color, low, middle, high in rows:
        if middle is None:
            continue
        figure.add_trace(go.Scatter(
            x=[middle],
            y=[label],
            mode="markers",
            name=label,
            marker={"color": color, "size": 10},
            error_x={
                "type": "data",
                "symmetric": False,
                "array": [high - middle if high is not None else 0],
                "arrayminus": [middle - low if low is not None else 0],
            },
            hovertemplate="%{y}: %{x:.4g}<extra></extra>",
        ))
    figure.update_layout(
        title={"text": html.escape(title)},
        height=180 + 35 * len(rows),
        margin={"l": 110, "r": 30, "t": 55, "b": 45},
        showlegend=False,
        template="plotly_white",
        yaxis={"autorange": "reversed"},
    )
    return figure.to_html(full_html=False, include_plotlyjs=False, config={"responsive": True})


def _legend(implementations: list[str]) -> str:
    items = "".join(
        f'<span class="swatch" style="background:{_color(index)}"></span>{html.escape(name)}'
        for index, name in enumerate(implementations)
    )
    return f'<div class="legend">{items}</div>'


def analyze_html(runs: list[Run]) -> str:
    """The shareable report of one or more runs."""
    body: list[str] = []
    for run in runs:
        body.append(f"<h1>gRPC benchmark run {html.escape(run.run_id)}</h1>")
        git = run.manifest.get("environment", {}).get("git", {})
        body.append(
            f"<p>Revision {html.escape(str(git.get('revision', 'unknown')))}"
            f"{' (dirty)' if git.get('dirty') else ''}"
            f", platform {html.escape(str(run.manifest.get('platform', 'unknown')))}"
            f", target {html.escape(str(run.manifest.get('target', 'unknown')))}"
            f", order {html.escape(str(run.manifest.get('order', 'unknown')))}"
            f", seed {html.escape(str(run.manifest.get('seed', '-')))}"
            f", repetitions {html.escape(str(run.manifest.get('repetitions', '-')))}</p>"
        )
        counts = implementation_counts(run.trials)
        body.append(
            "<p>Trials: "
            + ", ".join(
                f"{html.escape(name)}: {entry['completed']} completed, {entry['failed']} failed"
                for name, entry in counts.items()
            )
            + "</p>"
        )

        for trial in run.trials:
            if trial.status != "completed":
                body.append(
                    f'<p class="failed">FAILED {html.escape(trial.implementation)} '
                    f"{html.escape(trial.benchmark)}/{html.escape(trial.case)}: "
                    f"{html.escape(trial.error or 'unknown error')}</p>"
                )

        implementations = [name for name in IMPLEMENTATIONS if name in run.implementations]
        body.append(_legend(implementations))
        body.append("")

        for (benchmark, case), trials in group_trials(run.trials).items():
            body.append(f"<h2>{html.escape(benchmark)} / {html.escape(case)}</h2>")
            chart_rows: dict[str, list[tuple]] = {metric: [] for metric in SUMMARY_METRICS}
            table_rows: list[list] = []
            for index, implementation in enumerate(implementations):
                completed = [
                    trial for trial in trials
                    if trial.implementation == implementation and trial.status == "completed"
                ]
                if not completed:
                    continue
                metrics = [trial_metrics(trial) for trial in completed]
                for metric in SUMMARY_METRICS:
                    values = [m[metric] for m in metrics if m.get(metric) is not None]
                    if not values:
                        continue
                    ordered = sorted(values)
                    median_value = median(ordered)
                    low, high = ordered[0], ordered[-1]
                    chart_rows[metric].append(
                        (implementation, _color(index), low, median_value, high)
                    )
                    table_rows.append([
                        implementation, METRIC_LABELS.get(metric, metric), median_value,
                        low, high, len(ordered),
                    ])
            if table_rows:
                body.append(tabulate(
                    table_rows,
                    headers=["implementation", "metric", "median", "min", "max", "trials"],
                    tablefmt="html",
                    floatfmt=".4g",
                ))

            metric_label = trials[0].latency_metric if trials else ""
            if metric_label:
                body.append(f"<p>Latency metric: {html.escape(metric_label)}.</p>")
            for metric in SUMMARY_METRICS:
                rows = chart_rows[metric]
                if not rows:
                    continue
                unit = METRIC_UNITS.get(metric, "")
                title = f"{METRIC_LABELS.get(metric, metric)}" + (f" ({unit})" if unit else "")
                body.append(_dot_plot(rows, title))
            body.append("")
    return _page(f"gRPC benchmark run", "\n".join(body))


def comparison_html(rows: list[ComparisonRow], notes: list[str], baseline_label: str, candidate_label: str) -> str:
    """The shareable report of one baseline-versus-candidate comparison."""
    body: list[str] = []
    body.append("<h1>gRPC benchmark comparison</h1>")
    body.append(f"<p>Baseline: {html.escape(baseline_label)}.</p>")
    body.append(f"<p>Candidate: {html.escape(candidate_label)}.</p>")
    body.append(
        "<p>The experimental unit is one trial. Each interval is the range of the trials of one"
        " side; the verdicts come from a bootstrap interval of the ratio of the medians.</p>"
    )
    body.append(_legend([baseline_label, candidate_label]))
    body.append("")

    current_workload = None
    for row in rows:
        workload = f"{row.benchmark} / {row.case}"
        if workload != current_workload:
            current_workload = workload
            body.append(f"<h2>{html.escape(workload)}</h2>")
        comparison = row.comparison
        low_b = min(comparison.baseline) if comparison.baseline else None
        high_b = max(comparison.baseline) if comparison.baseline else None
        low_c = min(comparison.candidate) if comparison.candidate else None
        high_c = max(comparison.candidate) if comparison.candidate else None
        plot_rows = [
            ("baseline", _color(0), low_b, comparison.baseline_median, high_b),
            ("candidate", _color(1), low_c, comparison.candidate_median, high_c),
        ]
        body.append(_dot_plot(plot_rows, row.label))
        verdict = html.escape(comparison.verdict)
        interval = ""
        if comparison.ratio_interval:
            interval = (
                f" Ratio of medians {comparison.ratio:.2f}x"
                f" (bootstrap 95% interval {comparison.ratio_interval[0]:.2f}x to"
                f" {comparison.ratio_interval[1]:.2f}x)."
            )
        p_value = f" Mann-Whitney p={comparison.p_value:.3f}." if comparison.p_value is not None else ""
        confidence = html.escape(comparison.confidence)
        direction = "lower is better" if row.lower_is_better else "higher is better"
        body.append(f"<p>{verdict} ({direction}, confidence {confidence}).{html.escape(interval)}{p_value}</p>")

    if notes:
        body.append("<h2>Exclusions</h2>")
        body.append(tabulate([[note] for note in notes], headers=["note"], tablefmt="html"))

    return _page("gRPC benchmark comparison", "\n".join(body))
