"""Self-contained HTML report with inline SVG charts.

The report carries its own style. It needs no network access and no assets
beyond the file itself.

Chart method: dot-and-interval plots (Cleveland dot plots). One dot is the
median of one implementation, and the interval line spans the trials of that
implementation. The palette is the validated three-slot categorical palette of
the dataviz reference: blue, orange, and aqua, fixed hue order, with a dark-mode
variant. Every chart sits next to a data table, and every mark carries a title
tooltip, so no identity depends on color alone.
"""

from __future__ import annotations

import html
import json
import math

from .artifacts import Run, format_duration, group_trials, implementation_counts, trial_metrics
from .report import (
    COMPARISON_METRICS,
    METRIC_LABELS,
    METRIC_UNITS,
    SUMMARY_METRICS,
    ComparisonRow,
    format_value,
)

# Fixed categorical hue order, validated for both light and dark surfaces.
SERIES_COLORS = ["#2a78d6", "#eb6834", "#1baf7a"]
SERIES_COLORS_DARK = ["#3987e5", "#d95926", "#199e70"]

TEXT_PRIMARY = "#0b0b0b"
TEXT_SECONDARY = "#52514e"
SURFACE = "#fcfcfb"
TEXT_PRIMARY_DARK = "#f0efec"
TEXT_SECONDARY_DARK = "#c3c2b7"
SURFACE_DARK = "#1a1a19"

IMPLEMENTATIONS = ["current", "legacy", "swift"]


def _color(index: int) -> str:
    return SERIES_COLORS[index % len(SERIES_COLORS)]


def _dark_color(index: int) -> str:
    return SERIES_COLORS_DARK[index % len(SERIES_COLORS_DARK)]


def _style() -> str:
    return f"""
<style>
.viz-root {{
    color-scheme: light;
    --surface-1: {SURFACE};
    --text-primary: {TEXT_PRIMARY};
    --text-secondary: {TEXT_SECONDARY};
    --grid: #e6e5e1;
    font-family: -apple-system, "Segoe UI", Roboto, "Helvetica Neue", sans-serif;
    max-width: 60rem;
    margin: 0 auto;
    padding: 1.5rem;
    background: var(--surface-1);
    color: var(--text-primary);
}}
@media (prefers-color-scheme: dark) {{
    :root:where(:not([data-theme="light"])) .viz-root {{
        color-scheme: dark;
        --surface-1: {SURFACE_DARK};
        --text-primary: {TEXT_PRIMARY_DARK};
        --text-secondary: {TEXT_SECONDARY_DARK};
        --grid: #33322e;
    }}
}}
:root[data-theme="dark"] .viz-root {{
    color-scheme: dark;
    --surface-1: {SURFACE_DARK};
    --text-primary: {TEXT_PRIMARY_DARK};
    --text-secondary: {TEXT_SECONDARY_DARK};
    --grid: #33322e;
}}
h1 {{ font-size: 1.4rem; margin: 0 0 .25rem; }}
h2 {{ font-size: 1.1rem; margin: 2rem 0 .5rem; border-bottom: 1px solid var(--grid); padding-bottom: .25rem; }}
h3 {{ font-size: 1rem; margin: 1.25rem 0 .25rem; }}
p {{ color: var(--text-secondary); margin: .25rem 0; }}
table {{ border-collapse: collapse; margin: .5rem 0 1rem; font-size: .85rem; }}
th, td {{ text-align: right; padding: .2rem .6rem; border-bottom: 1px solid var(--grid); }}
th:first-child, td:first-child {{ text-align: left; }}
svg text {{ fill: var(--text-secondary); font-size: 11px; }}
svg .dot {{ stroke: var(--surface-1); stroke-width: 2px; }}
svg .interval {{ stroke-width: 2px; }}
svg .gridline {{ stroke: var(--grid); stroke-width: 1px; }}
.legend {{ font-size: .8rem; color: var(--text-secondary); margin: .25rem 0; }}
.legend .swatch {{ display: inline-block; width: 10px; height: 10px; border-radius: 2px; margin-right: .3rem; vertical-align: -1px; }}
.failed {{ color: #b3261e; }}
</style>
"""


def _page(title: str, body: str) -> str:
    return f"""<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>{html.escape(title)}</title>
{_style()}
</head>
<body>
<div class="viz-root">
{body}
</div>
</body>
</html>
"""


def _svg_dot_plot(rows: list[tuple[str, str, float | None, float | None, float | None]], title: str) -> str:
    """One horizontal dot-and-interval chart.

    Each row of `rows` is (label, color, low, median, high) in plot order from
    the top. A missing median renders an empty row with an explicit note.
    """
    height_per_row = 34
    width = 560
    label_width = 120
    value_width = 90
    plot_width = width - label_width - value_width
    top = 28
    height = top + height_per_row * len(rows) + 10

    values = [median for _, _, _, median, _ in rows if median is not None]
    lows = [low for _, _, low, _, _ in rows if low is not None]
    highs = [high for _, _, _, _, high in rows if high is not None]
    if not values:
        return f"<p>{html.escape(title)}: no data</p>"

    minimum = min(lows + values)
    maximum = max(highs + values)
    if minimum == maximum:
        if minimum == 0:
            maximum = 1.0
        else:
            minimum, maximum = minimum * 0.9, maximum * 1.1
    span = maximum - minimum

    def x_of(value: float) -> float:
        return label_width + (value - minimum) / span * plot_width

    parts = [
        f'<svg viewBox="0 0 {width} {height}" role="img" aria-label="{html.escape(title)}">',
        f"<text x=\"0\" y=\"16\">{html.escape(title)}</text>",
    ]
    for column in range(1, 5):
        fraction = minimum + span * column / 4
        x = x_of(fraction)
        parts.append(f'<line class="gridline" x1="{x:.1f}" y1="{top - 6}" x2="{x:.1f}" y2="{height - 10}"/>')
        parts.append(f'<text x="{x:.1f}" y="{height - 0}">{_short_number(fraction)}</text>')
    for index, (label, color, low, median, high) in enumerate(rows):
        y = top + height_per_row * index + height_per_row // 2
        parts.append(f'<text x="0" y="{y + 4}">{html.escape(label)}</text>')
        if median is None:
            parts.append(f'<text x="{label_width}" y="{y + 4}">no data</text>')
            continue
        if low is not None and high is not None and low != high:
            parts.append(
                f'<line class="interval" x1="{x_of(low):.1f}" y1="{y}" x2="{x_of(high):.1f}" y2="{y}"'
                f' style="stroke:{color}"/>'
            )
        parts.append(
            f'<circle class="dot" cx="{x_of(median):.1f}" cy="{y}" r="5" fill="{color}">'
            f"<title>{html.escape(label)}: median {_short_number(median)}"
            + (f", range {_short_number(low)} to {_short_number(high)}" if low is not None else "")
            + "</title></circle>"
        )
        parts.append(f'<text x="{width - value_width}" y="{y + 4}">{_short_number(median)}</text>')
    parts.append("</svg>")
    return "\n".join(parts)


def _short_number(value: float) -> str:
    if value is None:
        return "-"
    if value >= 1_000_000:
        return f"{value / 1_000_000:.2f}M"
    if value >= 1_000:
        return f"{value / 1_000:.1f}k"
    if value >= 100:
        return f"{value:.0f}"
    if value >= 10:
        return f"{value:.1f}"
    return f"{value:.2f}"


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
            table_rows: list[str] = []
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
                    median_value = ordered[len(ordered) // 2]
                    low, high = ordered[0], ordered[-1]
                    chart_rows[metric].append(
                        (implementation, _color(index), low, median_value, high)
                    )
                    table_rows.append(
                        f"<tr><td>{html.escape(implementation)}</td>"
                        f"<td>{html.escape(METRIC_LABELS.get(metric, metric))}</td>"
                        f"<td>{_short_number(median_value)}</td>"
                        f"<td>{_short_number(low)}</td>"
                        f"<td>{_short_number(high)}</td>"
                        f"<td>{len(ordered)}</td></tr>"
                    )
            if table_rows:
                body.append(
                    "<table><tr><th>implementation</th><th>metric</th><th>median</th>"
                    f"<th>min</th><th>max</th><th>trials</th></tr>{''.join(table_rows)}</table>"
                )

            metric_label = trials[0].latency_metric if trials else ""
            if metric_label:
                body.append(f"<p>Latency metric: {html.escape(metric_label)}.</p>")
            for metric in SUMMARY_METRICS:
                rows = chart_rows[metric]
                if not rows:
                    continue
                unit = METRIC_UNITS.get(metric, "")
                title = f"{METRIC_LABELS.get(metric, metric)}" + (f" ({unit})" if unit else "")
                body.append(_svg_dot_plot(rows, title))
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
        # Short row labels keep the plot area clear; the legend above the
        # charts carries the full run identity.
        plot_rows = [
            ("baseline", _color(0), low_b, comparison.baseline_median, high_b),
            ("candidate", _color(1), low_c, comparison.candidate_median, high_c),
        ]
        body.append(_svg_dot_plot(plot_rows, row.label))
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
        body.append("<table><tr><th>note</th></tr>")
        for note in notes:
            body.append(f"<tr><td>{html.escape(note)}</td></tr>")
        body.append("</table>")

    return _page("gRPC benchmark comparison", "\n".join(body))
