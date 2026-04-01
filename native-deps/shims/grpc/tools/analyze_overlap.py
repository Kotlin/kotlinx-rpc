#!/usr/bin/env python3
#
# Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#

from __future__ import annotations

import argparse
import shutil
import subprocess
import sys
import tempfile
import zipfile
from dataclasses import dataclass
from pathlib import Path


DEFAULT_SYMBOL_FILTERS = (
    "Absl",
    "utf8_range_",
    "_utf8_range_",
    "_ZN6google8protobuf",
    "_ZTIN6google8protobuf",
    "_ZTSN6google8protobuf",
    "_ZTVN6google8protobuf",
)

ARCHIVES_REQUIRING_MANUAL_HANDLING = {
    "abseil-cpp+__absl__debugging__libsymbolize.a": (
        # KRPC-540 temporary workaround note.
        # Remove this special-case guidance once protobuf becomes Kotlin-only and the native
        # protobuf/absl archive overlap disappears.
        "grpc and protobuf currently bundle different Abseil LTS namespaces, so excluding the "
        "whole libsymbolize archive can remove the only provider of grpc's absl::...::Symbolize. "
        "Handle duplicate plain C helper symbols in protobuf-shim instead."
    ),
}


@dataclass(frozen=True)
class ArchiveOverlap:
    archive_name: str
    symbols: tuple[str, ...]


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description=(
            "Report symbol overlap between the protobuf shim fat archive and the grpc shim's "
            "individual bundled archives."
        )
    )
    parser.add_argument("--grpc-klib", required=True, help="Path to the grpc shim cinterop KLIB")
    parser.add_argument("--protobuf-klib", required=True, help="Path to the protobuf shim cinterop KLIB")
    parser.add_argument(
        "--target",
        required=True,
        help="Target directory inside the KLIB, for example linux_x64 or macos_arm64",
    )
    parser.add_argument(
        "--symbol-prefix",
        action="append",
        default=[],
        help="Only report overlaps whose symbol name starts with this prefix. Can be passed multiple times.",
    )
    parser.add_argument(
        "--write-excludes",
        help=(
            "Write the matching archive names to this file, one per line. Existing comments and blank lines "
            "are preserved."
        ),
    )
    parser.add_argument(
        "--exclude-scope",
        default=None,
        help=(
            "Scope prefix to use together with --write-excludes, for example linux_x64 or all. "
            "Defaults to the value passed to --target."
        ),
    )
    return parser.parse_args()


def fail(message: str) -> "NoReturn":
    print(message, file=sys.stderr)
    raise SystemExit(1)


def find_llvm_nm() -> str:
    for candidate in ("llvm-nm", "nm"):
        path = shutil.which(candidate)
        if path:
            return path
    fail("Could not find llvm-nm or nm on PATH")


def unzip_klib(path: Path, destination: Path) -> None:
    with zipfile.ZipFile(path) as archive:
        archive.extractall(destination)


def list_symbols(nm_path: str, archive_path: Path) -> set[str]:
    completed = subprocess.run(
        [nm_path, "--defined-only", "--extern-only", "--format=posix", str(archive_path)],
        check=True,
        text=True,
        capture_output=True,
    )
    symbols: set[str] = set()
    for raw_line in completed.stdout.splitlines():
        line = raw_line.strip()
        if not line:
            continue
        name = line.split(maxsplit=1)[0]
        if name.endswith(":"):
            continue
        symbols.add(name)
    return symbols


def choose_protobuf_archive(included_dir: Path) -> Path:
    matches = sorted(included_dir.glob("libprotowire_fat.*.a"))
    if len(matches) != 1:
        fail(f"Expected exactly one libprotowire_fat archive under {included_dir}, found {len(matches)}")
    return matches[0]


def matching_symbols(symbols: set[str], prefixes: tuple[str, ...]) -> list[str]:
    return sorted(symbol for symbol in symbols if any(symbol.startswith(prefix) for prefix in prefixes))


def write_excludes(path: Path, archive_names: list[str], scope: str) -> None:
    existing_lines = path.read_text().splitlines() if path.exists() else []
    preserved = [line for line in existing_lines if not line.strip() or line.lstrip().startswith("#")]
    new_lines = preserved + [f"{scope}:{archive_name}" for archive_name in archive_names]
    path.write_text("\n".join(new_lines) + "\n")


def main() -> int:
    args = parse_args()
    nm_path = find_llvm_nm()
    prefixes = tuple(args.symbol_prefix) if args.symbol_prefix else DEFAULT_SYMBOL_FILTERS

    grpc_klib = Path(args.grpc_klib).resolve()
    protobuf_klib = Path(args.protobuf_klib).resolve()
    if not grpc_klib.is_file():
        fail(f"grpc KLIB not found: {grpc_klib}")
    if not protobuf_klib.is_file():
        fail(f"protobuf KLIB not found: {protobuf_klib}")

    with tempfile.TemporaryDirectory(prefix="grpc-proto-overlap-") as temp_dir_str:
        temp_dir = Path(temp_dir_str)
        grpc_dir = temp_dir / "grpc"
        protobuf_dir = temp_dir / "protobuf"
        unzip_klib(grpc_klib, grpc_dir)
        unzip_klib(protobuf_klib, protobuf_dir)

        grpc_included_dir = grpc_dir / "default" / "targets" / args.target / "included"
        protobuf_included_dir = protobuf_dir / "default" / "targets" / args.target / "included"
        if not grpc_included_dir.is_dir():
            fail(f"grpc included dir not found: {grpc_included_dir}")
        if not protobuf_included_dir.is_dir():
            fail(f"protobuf included dir not found: {protobuf_included_dir}")

        protobuf_archive = choose_protobuf_archive(protobuf_included_dir)
        protobuf_symbols = list_symbols(nm_path, protobuf_archive)

        overlaps: list[ArchiveOverlap] = []
        manual_notes: list[tuple[str, str]] = []
        for archive_path in sorted(grpc_included_dir.glob("*.a")):
            archive_symbols = list_symbols(nm_path, archive_path)
            shared = protobuf_symbols & archive_symbols
            interesting = matching_symbols(shared, prefixes)
            if interesting:
                note = ARCHIVES_REQUIRING_MANUAL_HANDLING.get(archive_path.name)
                if note is not None:
                    manual_notes.append((archive_path.name, note))
                    continue
                overlaps.append(ArchiveOverlap(archive_path.name, tuple(interesting)))

        print(f"Target: {args.target}")
        print(f"grpc klib: {grpc_klib}")
        print(f"protobuf klib: {protobuf_klib}")
        print(f"protobuf archive: {protobuf_archive.name}")
        print(f"symbol prefixes: {', '.join(prefixes)}")
        print()

        if not overlaps:
            print("No matching overlap found.")
        else:
            print("Candidate grpc archives to exclude:")
            for overlap in overlaps:
                print(f"- {overlap.archive_name} ({len(overlap.symbols)} symbols)")
                for symbol in overlap.symbols:
                    print(f"  {symbol}")

        if manual_notes:
            print()
            print("Archives requiring manual handling:")
            for archive_name, note in manual_notes:
                print(f"- {archive_name}")
                print(f"  {note}")

        if args.write_excludes:
            exclude_path = Path(args.write_excludes).resolve()
            exclude_scope = args.exclude_scope or args.target
            write_excludes(exclude_path, [overlap.archive_name for overlap in overlaps], exclude_scope)
            print()
            print(f"Wrote {len(overlaps)} archive names to {exclude_path} using scope {exclude_scope}")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
