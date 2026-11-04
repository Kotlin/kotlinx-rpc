"""Command-line entry for the offline analysis tools.

Called by the `bench` script:

    python3 analysis/cli.py analyze RUN_DIR [--format table|csv] [--output FILE]
    python3 analysis/cli.py compare BASELINE CANDIDATE [--format table|csv] [--output FILE]
    python3 analysis/cli.py equivalence MATRIX_JSON MATRIX_JSON ...
    python3 analysis/cli.py order SEED COUNT
"""

from __future__ import annotations

import argparse
import json
import random
import sys
from pathlib import Path

# The `bench` script runs this file directly, so the package must be resolvable
# without `python3 -m analysis.cli`.
if __package__ in (None, ""):
    sys.path.insert(0, str(Path(__file__).resolve().parent.parent))
    __package__ = "analysis"

from .artifacts import ArtifactError, load_run
from .html_report import analyze_html, comparison_html
from .report import (
    comparison_csv,
    comparison_table,
    compare_runs,
    run_summary_csv,
    summarize_run,
)

EXIT_OK = 0
EXIT_USAGE = 2
EXIT_NO_COMPARISON = 3


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(prog="bench-analysis", description=__doc__)
    commands = parser.add_subparsers(dest="command", required=True)

    analyze = commands.add_parser("analyze", help="Summarize saved run directories offline.")
    analyze.add_argument("runs", nargs="+", help="Run directories created by 'bench run'.")
    analyze.add_argument("--format", choices=["table", "csv", "html"], default="table")
    analyze.add_argument("--output", help="Write the report to this file instead of stdout.")

    compare = commands.add_parser("compare", help="Compare two implementations from saved runs.")
    compare.add_argument("baseline", help="Run directory, or RUN_DIR:IMPLEMENTATION.")
    compare.add_argument("candidate", help="Run directory, or RUN_DIR:IMPLEMENTATION.")
    compare.add_argument("--format", choices=["table", "csv", "html"], default="table")
    compare.add_argument("--output", help="Write the report to this file instead of stdout.")
    compare.add_argument(
        "--allow-injected-delay-mismatch",
        action="store_true",
        help="Pair runs whose injected delay differs. For the positive-control workflow.",
    )

    equivalence = commands.add_parser("equivalence", help="Compare workload matrices of two or more clients.")
    equivalence.add_argument("matrices", nargs="+", help="Output files of 'list --format json'.")

    order = commands.add_parser("order", help="Print a seeded shuffled order for the trial indices.")
    order.add_argument("seed", type=int)
    order.add_argument("count", type=int)

    arguments = parser.parse_args(argv)
    if arguments.command == "analyze":
        return _analyze(arguments)
    if arguments.command == "compare":
        return _compare(arguments)
    if arguments.command == "equivalence":
        return _equivalence(arguments)
    if arguments.command == "order":
        indices = list(range(arguments.count))
        random.Random(arguments.seed).shuffle(indices)
        print(" ".join(str(index) for index in indices))
        return EXIT_OK
    return EXIT_USAGE


def _analyze(arguments) -> int:
    try:
        runs = [load_run(run_dir) for run_dir in arguments.runs]
    except ArtifactError as error:
        print(f"error: {error}", file=sys.stderr)
        return EXIT_USAGE

    if arguments.format == "csv":
        text = run_summary_csv(runs)
    elif arguments.format == "html":
        if not arguments.output:
            print("error: --format html needs --output FILE.html", file=sys.stderr)
            return EXIT_USAGE
        _write(analyze_html(runs), arguments.output)
        return EXIT_OK
    else:
        text = "\n\n".join(summarize_run(run) for run in runs)
    _emit(text, arguments.output)
    return EXIT_OK


def _compare(arguments) -> int:
    try:
        baseline_runs, baseline_label, baseline_implementation = _select_side(arguments.baseline)
        candidate_runs, candidate_label, candidate_implementation = _select_side(arguments.candidate)
    except ArtifactError as error:
        print(f"error: {error}", file=sys.stderr)
        return EXIT_USAGE

    rows, notes = compare_runs(
        baseline_runs, candidate_runs, baseline_implementation, candidate_implementation,
        tolerate_injected_delay=arguments.allow_injected_delay_mismatch,
    )
    if not rows:
        print("error: no comparable trials. The workload, platform, target, and injected delay must match.", file=sys.stderr)
        return EXIT_NO_COMPARISON
    if arguments.format == "csv":
        text = comparison_csv(rows, baseline_label, candidate_label)
    elif arguments.format == "html":
        if not arguments.output:
            print("error: --format html needs --output FILE.html", file=sys.stderr)
            return EXIT_USAGE
        _write(comparison_html(rows, notes, baseline_label, candidate_label), arguments.output)
        return EXIT_OK
    else:
        text = comparison_table(rows, notes, baseline_label, candidate_label)
    _emit(text, arguments.output)
    return EXIT_OK


def _select_side(selector: str) -> tuple[list, str, str]:
    """Parses RUN_DIR or RUN_DIR:IMPLEMENTATION into the runs and the implementation."""
    if ":" in selector and not Path(selector).is_dir():
        run_dir, _, implementation = selector.rpartition(":")
    elif Path(selector).is_dir() and ":" in selector.split("/")[-1]:
        # A trailing ':implementation' on an existing directory.
        run_dir, _, implementation = selector.rpartition(":")
    else:
        run_dir, implementation = selector, None

    run = load_run(run_dir)
    if implementation is None:
        unique = run.implementations
        if len(unique) != 1:
            raise ArtifactError(
                f"'{run_dir}' holds results of {len(unique)} implementations {unique}. "
                "Select one with RUN_DIR:IMPLEMENTATION."
            )
        implementation = unique[0]
    if implementation not in run.implementations:
        raise ArtifactError(
            f"'{run_dir}' holds no results of '{implementation}'. Available: {run.implementations}."
        )
    label = f"{run.run_id}:{implementation}"
    return [run], label, implementation


def _equivalence(arguments) -> int:
    """Compares the benchmark and case definitions of two or more workload matrices.

    The descriptions can differ between clients. The benchmarks, cases,
    parameters, latency metrics, and operation units must not.
    """
    if len(arguments.matrices) < 2:
        print("error: equivalence needs at least two matrix files.", file=sys.stderr)
        return EXIT_USAGE

    def canonical(path: str):
        with open(path, encoding="utf-8") as file:
            matrix = json.load(file)
        return [
            {
                "benchmark": entry["benchmark"],
                "latency_metric": entry["latency_metric"],
                "operation_unit": entry["operation_unit"],
                "cases": [
                    {"case": case["case"], "parameters": case["parameters"]}
                    for case in entry["cases"]
                ],
            }
            for entry in matrix
        ]

    reference_path = arguments.matrices[0]
    reference = canonical(reference_path)
    mismatches = 0
    for path in arguments.matrices[1:]:
        other = canonical(path)
        if other != reference:
            mismatches += 1
            print(f"workload mismatch: {reference_path} and {path}", file=sys.stderr)
            reference_benchmarks = [entry["benchmark"] for entry in reference]
            other_benchmarks = [entry["benchmark"] for entry in other]
            for benchmark in set(reference_benchmarks) - set(other_benchmarks):
                print(f"  only in {reference_path}: {benchmark}", file=sys.stderr)
            for benchmark in set(other_benchmarks) - set(reference_benchmarks):
                print(f"  only in {path}: {benchmark}", file=sys.stderr)
            for reference_entry, other_entry in zip(reference, other):
                if reference_entry == other_entry:
                    continue
                if reference_entry["benchmark"] != other_entry["benchmark"]:
                    continue
                for field in ("latency_metric", "operation_unit"):
                    if reference_entry[field] != other_entry[field]:
                        print(
                            f"  {reference_entry['benchmark']}: {field} is "
                            f"{reference_entry[field]!r} in {reference_path} and {other_entry[field]!r} in {path}",
                            file=sys.stderr,
                        )
                reference_cases = {case["case"]: case["parameters"] for case in reference_entry["cases"]}
                other_cases = {case["case"]: case["parameters"] for case in other_entry["cases"]}
                for case in set(reference_cases) - set(other_cases):
                    print(f"  {reference_entry['benchmark']}: case '{case}' only in {reference_path}", file=sys.stderr)
                for case in set(other_cases) - set(reference_cases):
                    print(f"  {other_entry['benchmark']}: case '{case}' only in {path}", file=sys.stderr)
                for case in set(reference_cases) & set(other_cases):
                    if reference_cases[case] != other_cases[case]:
                        print(
                            f"  {reference_entry['benchmark']}/{case}: parameters "
                            f"{reference_cases[case]} in {reference_path} vs {other_cases[case]} in {path}",
                            file=sys.stderr,
                        )
    if mismatches:
        print(f"{mismatches} client pair(s) differ. Align the benchmark definitions before comparing results.", file=sys.stderr)
        return 1
    print(f"workload matrices agree across {len(arguments.matrices)} clients")
    return EXIT_OK


def _write(text: str, output: str) -> None:
    with open(output, "w", encoding="utf-8") as file:
        file.write(text)
        file.write("\n")


def _emit(text: str, output: str | None) -> None:
    if output:
        with open(output, "w", encoding="utf-8") as file:
            file.write(text)
            file.write("\n")
    else:
        print(text)


if __name__ == "__main__":
    sys.exit(main())
