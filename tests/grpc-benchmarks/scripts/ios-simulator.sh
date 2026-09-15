# Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

select_ios_simulator() {
    local state="$1"
    local device_family="${2:-}"

    awk -v state="${state}" -v device_family="${device_family}" '
        /^-- iOS / { ios_runtime = 1; next }
        /^-- / { ios_runtime = 0; next }
        ios_runtime && $0 ~ "\\(" state "\\)[[:space:]]*$" {
            if (device_family != "" && $0 !~ "^[[:space:]]+" device_family) next
            if (match($0, /\([[:xdigit:]-]+\)/)) {
                print substr($0, RSTART + 1, RLENGTH - 2)
                exit
            }
        }
    '
}

ios_simulator_exists() {
    local simulator="$1"

    awk -v simulator="${simulator}" 'index($0, "(" simulator ")") { found = 1 } END { exit !found }'
}

ios_simulator_is_booted() {
    local simulator="$1"

    awk -v simulator="${simulator}" '
        index($0, "(" simulator ")") && /\(Booted\)[[:space:]]*$/ { found = 1 }
        END { exit !found }
    '
}

ensure_ios_simulator() {
    local devices
    local simulator
    local requested_simulator="${KXRPC_BENCHMARK_SIMULATOR:-}"

    devices="$(xcrun simctl list devices available)"

    if [[ -n "${requested_simulator}" && "${requested_simulator}" != "booted" ]]; then
        simulator="${requested_simulator}"
        if ! ios_simulator_exists "${simulator}" <<< "${devices}"; then
            echo "iOS Simulator '${simulator}' is not available." >&2
            exit 2
        fi
    else
        simulator="$(select_ios_simulator Booted <<< "${devices}")"
        if [[ -z "${simulator}" ]]; then
            simulator="$(select_ios_simulator Shutdown iPhone <<< "${devices}")"
        fi
        if [[ -z "${simulator}" ]]; then
            simulator="$(select_ios_simulator Shutdown <<< "${devices}")"
        fi
        if [[ -z "${simulator}" ]]; then
            echo "No available iOS Simulator device found. Install an iOS Simulator runtime in Xcode." >&2
            exit 2
        fi
    fi

    if ! ios_simulator_is_booted "${simulator}" <<< "${devices}"; then
        echo "Booting iOS Simulator ${simulator}..." >&2
        xcrun simctl boot "${simulator}" >&2
    fi

    xcrun simctl bootstatus "${simulator}" -b >&2
    printf '%s\n' "${simulator}"
}
