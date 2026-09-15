# Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

run_transient_command() {
    local label="$1"
    shift

    if [[ "${KXRPC_BENCHMARK_VERBOSE_BUILD:-0}" == "1" || ! -t 2 ]]; then
        "$@" >&2
        return
    fi

    local command_log
    local command_status
    command_log="$(mktemp "${TMPDIR:-/tmp}/kxrpc-benchmark-build.XXXXXX")"

    printf '\r\033[2K[build] %s' "${label}" >&2

    if "$@" > "${command_log}" 2>&1; then
        command_status=0
    else
        command_status=$?
    fi

    printf '\r\033[2K' >&2

    if [[ ${command_status} -ne 0 ]]; then
        printf '[build] Failed: %s\n' "${label}" >&2
        cat "${command_log}" >&2
    fi
    rm -f -- "${command_log}"
    return "${command_status}"
}
