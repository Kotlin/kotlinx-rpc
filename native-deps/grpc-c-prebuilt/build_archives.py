#!/usr/bin/env python3
#
# Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#

from __future__ import annotations

import ast
import os
import shutil
import subprocess
import sys
from pathlib import Path
from typing import Iterable


CONFIG = "release"
QUERY_EXPR = """
[
  (
    lib.pic_static_library.short_path if lib.pic_static_library else lib.static_library.short_path,
    lib.pic_static_library.path if lib.pic_static_library else lib.static_library.path,
    [file.path for file in (lib.pic_objects if lib.pic_static_library else lib.objects)]
  )
  for linker_input in providers(target)["CcInfo"].linking_context.linker_inputs.to_list()
  for lib in linker_input.libraries
  if lib.pic_static_library or lib.static_library
]
""".strip()


def fail(message: str) -> "NoReturn":
    print(message, file=sys.stderr)
    raise SystemExit(1)


def require_arg(args: list[str], index: int, message: str) -> str:
    try:
        return args[index]
    except IndexError:
        fail(message)


def log(message: str) -> None:
    print(f"==> {message}", file=sys.stderr)


def script_dir() -> Path:
    return Path(__file__).resolve().parent


def resolve_konan_llvm_resource_dir(konan_deps: str, konan_home: str) -> str:
    environment = dict(os.environ)
    environment["BAZEL_KONAN_DEPS"] = konan_deps
    tool = script_dir().parent / "bazel-support" / "toolchain" / "resolve_konan_llvm_resource_dir.py"
    completed = subprocess.run(
        [str(tool), konan_home],
        check=True,
        text=True,
        capture_output=True,
        env=environment,
    )
    return completed.stdout.strip()


def run(command: list[str], *, capture_output: bool = False) -> subprocess.CompletedProcess[str]:
    print(f"+ {shlex_join(command)}", file=sys.stderr)
    return subprocess.run(
        command,
        check=True,
        text=True,
        capture_output=capture_output,
    )


def shlex_join(parts: Iterable[str]) -> str:
    return " ".join(shlex_quote(part) for part in parts)


def shlex_quote(value: str) -> str:
    if not value:
        return "''"
    if all(character.isalnum() or character in "@%_-+=:,./" for character in value):
        return value
    return "'" + value.replace("'", "'\"'\"'") + "'"


def normalize_bundle_relative_path(path: Path) -> Path:
    if path.suffix == ".lo":
        return path.with_suffix(".a")
    return path


def to_bundle_relative_path(short_path: str) -> Path:
    normalized_short_path = short_path[3:] if short_path.startswith("../") else short_path
    return normalize_bundle_relative_path(Path(normalized_short_path))


def pack_archive_from_objects(destination: Path, short_path: str, object_paths: list[str], llvm_ar: Path) -> None:
    materialized_objects = [Path(object_path) for object_path in object_paths]
    missing_objects = [str(object_path) for object_path in materialized_objects if not object_path.exists()]
    if missing_objects:
        raise RuntimeError(
            f"Archive {short_path} is not materialized and some object files are missing: {missing_objects}"
        )
    if not materialized_objects:
        raise RuntimeError(f"Archive {short_path} is not materialized and has no object files to pack")

    subprocess.run(
        [str(llvm_ar), "rcs", str(destination), *(str(object_path) for object_path in materialized_objects)],
        check=True,
    )


def materialize_archive(
    short_path: str,
    archive_path: str,
    object_paths: list[str],
    lib_root: Path,
    llvm_ar: Path,
) -> Path:
    relative_path = to_bundle_relative_path(short_path)
    destination = lib_root / relative_path
    destination.parent.mkdir(parents=True, exist_ok=True)

    source = Path(archive_path)
    if source.exists():
        shutil.copy2(source, destination)
    else:
        pack_archive_from_objects(destination, short_path, object_paths, llvm_ar)

    return relative_path


def main(argv: list[str]) -> int:
    label = require_arg(argv, 1, "need bazel target label")
    destination_root = Path(require_arg(argv, 2, "need output destination directory"))
    konan_target = require_arg(argv, 3, "need the konan_target name")
    konan_home = require_arg(argv, 4, "need the konan_home path")

    destination_root.mkdir(parents=True, exist_ok=True)

    konan_deps = os.environ.get("KONAN_DEPS", str(Path(konan_home).parent / "dependencies"))
    konan_llvm_resource_dir = resolve_konan_llvm_resource_dir(konan_deps, konan_home)
    llvm_ar = (Path(konan_llvm_resource_dir).parent.parent.parent / "bin" / "llvm-ar").resolve()

    bazel_args = [
        f"--config={konan_target}",
        f"--config={CONFIG}",
        f"--define=KONAN_DEPS={konan_deps}",
        f"--define=KONAN_LLVM_RESOURCE_DIR={konan_llvm_resource_dir}",
        f"--define=KONAN_HOME={konan_home}",
    ]

    log(f"Building {label} archives into {destination_root}")
    log(f"KONAN_HOME: {konan_home}")
    log(f"KONAN_TARGET: {konan_target}")
    log(f"KONAN_DEPS: {konan_deps}")
    log(f"KONAN_LLVM_RESOURCE_DIR: {konan_llvm_resource_dir}")

    run(["bazel", "build", label, *bazel_args])

    archive_tuples_output = run(
        [
            "bazel",
            "cquery",
            label,
            *bazel_args,
            "--output=starlark",
            f"--starlark:expr={QUERY_EXPR}",
        ],
        capture_output=True,
    ).stdout

    archive_tuples = ast.literal_eval(archive_tuples_output)
    lib_root = destination_root / "lib"
    manifest = destination_root / "metadata" / "archives.txt"

    if lib_root.exists():
        shutil.rmtree(lib_root)
    manifest.parent.mkdir(parents=True, exist_ok=True)
    lib_root.mkdir(parents=True, exist_ok=True)

    manifest_lines: list[str] = []
    seen: set[str] = set()

    for short_path, archive_path, object_paths in archive_tuples:
        relative = to_bundle_relative_path(short_path)
        destination_key = str(lib_root / relative)
        if destination_key not in seen:
            materialize_archive(short_path, archive_path, object_paths, lib_root, llvm_ar)
            seen.add(destination_key)
        manifest_lines.append(f"lib/{relative.as_posix()}")

    manifest.write_text("\n".join(manifest_lines) + "\n")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main(sys.argv))
    except subprocess.CalledProcessError:
        print(f"ERROR: Build failed at {__file__}", file=sys.stderr)
        raise
    except Exception:
        print(f"ERROR: Build failed at {__file__}", file=sys.stderr)
        raise
