# Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

"""Writes the run manifest of one `bench run` invocation.

The values come from the environment. The trial list is a file with one JSON
object per line. Keeping the manifest generation here avoids shell quoting
problems with benchmark and case names.
"""

from __future__ import annotations

import json
import os
import subprocess
import sys


def repository_output(*arguments: str) -> str:
    result = subprocess.run(
        arguments,
        capture_output=True,
        text=True,
        check=False,
        cwd=os.environ.get("KXRPC_BENCH_REPOSITORY_ROOT", "."),
    )
    return result.stdout.strip() or "unknown"


def main() -> int:
    if len(sys.argv) != 4:
        print("usage: write_manifest.py RUN_DIR TRIAL_LIST_JSON PASSTHROUGH_JSON", file=sys.stderr)
        return 2
    run_dir, trial_list_path, passthrough_path = sys.argv[1:4]

    with open(trial_list_path, encoding="utf-8") as file:
        trials = [json.loads(line) for line in file if line.strip()]
    with open(passthrough_path, encoding="utf-8") as file:
        client_arguments = json.load(file)

    def setting(name: str, default: str) -> str:
        return os.environ.get(name, default)

    def number(name: str, default: str) -> int:
        try:
            return int(os.environ.get(name, default))
        except ValueError:
            return int(default)

    repository_root = setting("KXRPC_BENCH_REPOSITORY_ROOT", ".")
    dirty = bool(
        subprocess.run(
            ["git", "status", "--porcelain"],
            capture_output=True,
            text=True,
            check=False,
            cwd=repository_root,
        ).stdout.strip()
    )

    manifest = {
        "schema_version": 1,
        "run_id": setting("KXRPC_BENCH_RUN_ID", "unknown"),
        "created_at_epoch_ms": number("KXRPC_BENCH_CREATED_AT_EPOCH_MS", "0"),
        "repetitions": number("KXRPC_BENCH_REPETITIONS", "0"),
        "order": setting("KXRPC_BENCH_ORDER", "random"),
        "seed": number("KXRPC_BENCH_SEED", "0"),
        "platform": setting("KXRPC_BENCH_PLATFORM_LABEL", "unknown"),
        "target": f"localhost:{number('KXRPC_BENCH_PORT', '50051')}",
        "simulator": setting("KXRPC_BENCH_SIMULATOR", "not applicable"),
        "implementations": [
            name.strip()
            for name in setting("KXRPC_BENCH_IMPLEMENTATIONS", "").split(",")
            if name.strip()
        ],
        "benchmark": setting("KXRPC_BENCH_BENCHMARK", "unknown"),
        "case": setting("KXRPC_BENCH_CASE", "") or None,
        "trial_timeout_seconds": number("KXRPC_BENCH_TRIAL_TIMEOUT", "900"),
        "gap_seconds": float(setting("KXRPC_BENCH_GAP_SECONDS", "0.5")),
        "inject_delay_ms": number("KXRPC_BENCH_INJECT_DELAY_MS", "0"),
        "client_arguments": client_arguments,
        "environment": {
            "git": {
                "revision": repository_output("git", "rev-parse", "HEAD"),
                "dirty": dirty,
            },
            "host": {
                "model": repository_output("sysctl", "-n", "hw.model"),
                "os": repository_output("sw_vers", "-productVersion"),
            },
            "xcode": repository_output("xcodebuild", "-version") or "unknown",
            "thermal": repository_output("pmset", "-g", "ther") or "unknown",
            "power": repository_output("pmset", "-g", "ps") or "unknown",
            "server": {
                "grpc_revision": "b8f09d9168d856020d236bd32f39195f7b5aa2cf",
                "max_message_bytes": 33554432,
                "server_type": "async",
            },
        },
        "trials": trials,
    }

    path = os.path.join(run_dir, "manifest.json")
    with open(path, "w", encoding="utf-8") as file:
        json.dump(manifest, file, indent=2)
        file.write("\n")
    os.remove(trial_list_path)
    return 0


if __name__ == "__main__":
    sys.exit(main())
