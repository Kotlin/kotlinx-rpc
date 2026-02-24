#!/usr/bin/env python3
"""
Precompute LLVM Konan bundle names per Kotlin compiler version.

Usage:
  ./toolchain/precompute_konan_llvm_bundles.py
  ./toolchain/precompute_konan_llvm_bundles.py 2.3.0
  ./toolchain/precompute_konan_llvm_bundles.py --update-all-known

Context:
  - Writes toolchain/konan_llvm_bundles.json consumed by resolve_konan_llvm_resource_dir.py.
  - Verified in CI by .github/workflows/verify-konan-llvm-bundles.yml.
  - Used indirectly by cinterop-c/build_target.sh and cinterop-c/extract_include_dir.sh.
"""

from __future__ import annotations

import argparse
import json
import os
import re
import sys
from datetime import datetime, timezone
from pathlib import Path
from urllib.error import URLError
from urllib.request import urlopen

SUPPORTED_HOSTS = ["linux-x86_64", "macos-aarch64", "macos-x86_64"]
SOURCE_TEMPLATE = "https://raw.githubusercontent.com/JetBrains/kotlin/v{version}/kotlin-native/gradle.properties"


def parse_args() -> argparse.Namespace:
    """Parse CLI arguments for version selection and update mode."""
    parser = argparse.ArgumentParser(
        description="Precompute LLVM bundle mapping for Kotlin compiler version(s)."
    )
    parser.add_argument(
        "versions",
        nargs="*",
        help="Kotlin compiler versions to refresh. Defaults to project's resolved kotlin-compiler version.",
    )
    parser.add_argument(
        "--update-all-known",
        action="store_true",
        help="Refresh all Kotlin versions currently present in konan_llvm_bundles.json.",
    )
    return parser.parse_args()


def parse_versions_file(path: Path) -> str | None:
    """Extract kotlin-lang value from versions-root/libs.versions.toml."""
    kotlin_lang: str | None = None
    # Keep parsing intentionally narrow to avoid requiring a TOML dependency:
    # this script only needs kotlin-lang from versions-root/libs.versions.toml.
    kotlin_lang_re = re.compile(r'^\s*kotlin-lang\s*=\s*"([^"]+)"\s*$')

    for raw_line in path.read_text(encoding="utf-8").splitlines():
        line = raw_line.split("#", 1)[0].strip()
        if not line:
            continue

        kotlin_lang_match = kotlin_lang_re.match(line)
        if kotlin_lang_match:
            kotlin_lang = kotlin_lang_match.group(1)
    return kotlin_lang


def resolve_default_kotlin_compiler_version(repo_root: Path) -> str:
    """Resolve the default compiler version, honoring env overrides and project fallback rules."""
    versions_file = repo_root / "versions-root" / "libs.versions.toml"
    parsed_kotlin_lang = parse_versions_file(versions_file)

    kotlin_lang = os.getenv("KOTLIN_VERSION") or parsed_kotlin_lang
    # Ignore kotlin-compiler from TOML and default to kotlin-lang unless explicitly overridden.
    kotlin_compiler = os.getenv("KOTLIN_COMPILER_VERSION") or kotlin_lang

    if not kotlin_lang:
        raise RuntimeError("Unable to resolve kotlin-lang version")
    if not kotlin_compiler or kotlin_compiler == "0.0.0":
        # Keep compatibility with explicit "0.0.0" env override semantics.
        kotlin_compiler = kotlin_lang

    return kotlin_compiler


def fetch_llvm_bundles(version: str) -> dict[str, str]:
    """Fetch Kotlin Native gradle.properties for a version and map host tuples to llvm bundle names."""
    url = SOURCE_TEMPLATE.format(version=version)
    try:
        with urlopen(url) as response:
            text = response.read().decode("utf-8")
    except URLError as exc:
        raise RuntimeError(f"Failed to download {url}: {exc}") from exc

    pattern = re.compile(r"(llvm-\d+-(?:aarch64|x86_64)-(?:macos|linux)-essentials-\d+)")
    bundles: dict[str, str] = {}
    for bundle in sorted(set(pattern.findall(text))):
        match = re.match(r"llvm-\d+-(aarch64|x86_64)-(macos|linux)-essentials-\d+", bundle)
        if not match:
            continue
        # Use the same "<os>-<arch>" tuple format used by KONAN_HOME parsing.
        host_key = f"{match.group(2)}-{match.group(1)}"
        current = bundles.get(host_key)
        if current and current != bundle:
            # Do not silently pick one if upstream format changes and yields conflicts.
            raise RuntimeError(
                f"Multiple LLVM bundles found for {host_key} in Kotlin {version}: {current}, {bundle}"
            )
        bundles[host_key] = bundle

    missing = [key for key in SUPPORTED_HOSTS if key not in bundles]
    if missing:
        raise RuntimeError(
            f"Missing LLVM bundles for Kotlin {version}: {', '.join(missing)}. Expected entries in {url}."
        )

    return {key: bundles[key] for key in sorted(bundles)}


def main() -> int:
    """Entry point: compute/update konan_llvm_bundles.json for requested Kotlin versions."""
    args = parse_args()
    script_dir = Path(__file__).resolve().parent
    repo_root = script_dir.parent.parent
    map_file = script_dir / "konan_llvm_bundles.json"

    if args.update_all_known and args.versions:
        raise RuntimeError("--update-all-known cannot be combined with explicit versions")

    try:
        data = json.loads(map_file.read_text(encoding="utf-8"))
    except FileNotFoundError:
        data = {}

    versions: list[str]
    if args.update_all_known:
        # Keys prefixed with "_" (for example "_metadata") are reserved for non-version entries.
        versions = sorted([k for k in data.keys() if not k.startswith("_")])
    elif args.versions:
        versions = args.versions
    else:
        versions = [resolve_default_kotlin_compiler_version(repo_root)]

    if not versions:
        raise RuntimeError("No Kotlin versions to update")

    print("Precomputing LLVM bundle mapping for Kotlin compiler version(s):", " ".join(versions))

    changed = False
    for version in versions:
        entry = fetch_llvm_bundles(version)
        if data.get(version) != entry:
            changed = True
            data[version] = entry
            print(f"Updated version: {version}")

    metadata = dict(data.get("_metadata", {}))
    # Keep source metadata in-file so updates are auditable without checking CI logs.
    metadata["source"] = "https://raw.githubusercontent.com/JetBrains/kotlin/v<kotlin-version>/kotlin-native/gradle.properties"
    metadata["supported_hosts"] = SUPPORTED_HOSTS
    if changed or "generated_at" not in metadata:
        metadata["generated_at"] = datetime.now(timezone.utc).replace(microsecond=0).isoformat().replace("+00:00", "Z")
    data["_metadata"] = metadata

    serialized = json.dumps(data, indent=2, sort_keys=True) + "\n"
    previous = map_file.read_text(encoding="utf-8") if map_file.exists() else ""

    if serialized != previous:
        map_file.write_text(serialized, encoding="utf-8")
        print(f"Wrote {map_file}")
    else:
        print("No changes")

    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except RuntimeError as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        raise SystemExit(1)
