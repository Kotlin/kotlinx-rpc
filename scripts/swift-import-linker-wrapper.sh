#!/usr/bin/env bash

# Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

set -euo pipefail

: "${KOTLIN_LD_ARGS_DUMP_FILE:?KOTLIN_LD_ARGS_DUMP_FILE must point to the Kotlin linker-argument directory}"

readonly dump_file="${KOTLIN_LD_ARGS_DUMP_FILE}/$(/usr/bin/uuidgen)"
driver="clang"

for argument in "$@"; do
    # Kotlin classifies every captured -l* argument as a native library. This Swift-driver flag
    # would consequently become a request for the nonexistent library "ink-objc-runtime".
    if [[ "${argument}" == @/*.LinkFileList ]]; then
        # Xcode 27 passes Swift object lists as response files, while Kotlin 2.4 recognizes only
        # Clang's two-argument spelling when extracting native linker options. Swift response files
        # are space-separated; Apple ld file lists require one input path per line.
        response_file="${argument#@}"
        normalized_file_list="${response_file}.kotlin-filelist"
        /usr/bin/tr ' ' '\n' < "${response_file}" > "${normalized_file_list}"
        printf '%s;%s;' "-filelist" "${normalized_file_list}" >> "${dump_file}"
    elif [[ "${argument}" != "-link-objc-runtime" ]]; then
        printf '%s;' "${argument}" >> "${dump_file}"
    fi
    if [[ "${argument}" == "-emit-library" ]]; then
        driver="swiftc"
    fi
done

readonly driver
exec "$(/usr/bin/xcrun --find "${driver}")" "$@"
