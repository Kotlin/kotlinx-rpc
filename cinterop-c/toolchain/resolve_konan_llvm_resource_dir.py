#!/usr/bin/env python3
"""
Resolve the clang resource include directory for the active KONAN_HOME.

Usage:
  ./toolchain/resolve_konan_llvm_resource_dir.py /path/to/kotlin-native-prebuilt-<host>-<arch>-<version>
  KONAN_HOME=/path/to/kotlin-native-prebuilt-<host>-<arch>-<version> ./toolchain/resolve_konan_llvm_resource_dir.py
"""

from __future__ import annotations

import json
import os
import re
import sys
from pathlib import Path


def fail(message: str) -> "NoReturn":
    print(f"ERROR: {message}", file=sys.stderr)
    raise SystemExit(1)


def parse_konan_home(konan_home: Path) -> tuple[str, str]:
    match = re.match(
        r"^kotlin-native-prebuilt-(?P<os>macos|linux)-(?P<arch>aarch64|arm64|x86_64|amd64)-(?P<version>.+)$",
        konan_home.name,
    )
    if not match:
        fail(
            f"KONAN_HOME basename '{konan_home.name}' must match "
            "kotlin-native-prebuilt-<host>-<arch>-<version>"
        )

    host_os = match.group("os")
    host_arch = match.group("arch")
    kotlin_version = match.group("version")

    if host_arch in {"arm64", "aarch64"}:
        host_arch = "aarch64"
    elif host_arch in {"amd64", "x86_64"}:
        host_arch = "x86_64"

    return kotlin_version, f"{host_os}-{host_arch}"


def main() -> int:
    script_dir = Path(__file__).resolve().parent
    map_file = script_dir / "konan_llvm_bundles.json"

    if not map_file.exists():
        fail(f"Missing mapping file: {map_file}")

    konan_home_input = sys.argv[1] if len(sys.argv) > 1 else os.getenv("KONAN_HOME")
    if not konan_home_input:
        fail("KONAN_HOME must be provided via argument or environment variable")

    konan_home = Path(konan_home_input).expanduser().resolve()
    konan_deps = Path(os.getenv("KONAN_DEPS", str(konan_home / ".." / "dependencies"))).expanduser().resolve()

    kotlin_version, host_tuple = parse_konan_home(konan_home)

    try:
        data = json.loads(map_file.read_text(encoding="utf-8"))
    except json.JSONDecodeError as exc:
        fail(f"Failed to parse {map_file}: {exc}")

    version_entry = data.get(kotlin_version)
    if not isinstance(version_entry, dict):
        fail(
            f"Missing precomputed LLVM mapping for Kotlin compiler version {kotlin_version}. "
            f"Run ./toolchain/precompute_konan_llvm_bundles.py {kotlin_version} and commit {map_file.name}."
        )

    bundle_name = version_entry.get(host_tuple)
    if not isinstance(bundle_name, str) or not bundle_name:
        fail(
            f"Mapping for Kotlin compiler version {kotlin_version} does not contain host tuple {host_tuple}. "
            f"Run ./toolchain/precompute_konan_llvm_bundles.py {kotlin_version} and commit {map_file.name}."
        )

    bundle_dir = konan_deps / bundle_name
    if not bundle_dir.is_dir():
        fail(
            f"Expected LLVM bundle directory is not installed: {bundle_dir}. "
            "Run Kotlin/Native dependency download (e.g. via ./gradlew) and ensure KONAN_HOME/KONAN_DEPS point to the active distribution."
        )

    include_dirs = [p for p in sorted(bundle_dir.glob("lib/clang/*/include")) if p.is_dir()]
    if not include_dirs:
        fail(f"No clang resource include directory found under {bundle_dir}/lib/clang/*/include")
    if len(include_dirs) != 1:
        lines = "\n".join(f" - {p}" for p in include_dirs)
        fail(
            f"Expected exactly one clang resource include directory, found {len(include_dirs)} under {bundle_dir}/lib/clang:\n{lines}"
        )

    print(include_dirs[0])
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
