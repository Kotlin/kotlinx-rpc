#!/bin/bash

#
# Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#

set -euo pipefail

function fancyEcho() {
    echo "[ARTIFACT_VALIDATION] $1"
}

function usageAndFail() {
    fancyEcho "Usage: "
    fancyEcho "./validatePublishedArtifacts.sh [options]"
    fancyEcho "Options:"
    fancyEcho "  -v --- Verbose Gradle output"
    fancyEcho "  -s --- Silent mode"
    fancyEcho "  --dump --- Dump artifacts"
    exit 1
}

silent=false
dump=false
verbose=false

function checkArgument() {
  if [[ "$1" != "--dump" ]] && [[ "$1" != "-v" ]] && [[ "$1" != "-s" ]]; then
      usageAndFail
  elif [[ "$1" == "--dump" ]]; then
      dump=true
  elif [[ "$1" == "-v" ]]; then
      verbose=true
  elif [[ "$1" == "-s" ]]; then
      silent=true
  fi
}

if [[ "$#" -gt 3 ]]; then
    usageAndFail
fi

if [[ "$#" -ge 1 ]]; then
    checkArgument "$1"
fi

if [[ "$#" -ge 2 ]]; then
    checkArgument "$2"
fi

if [[ "$#" -ge 3 ]]; then
    checkArgument "$3"
fi

fancyEcho "Dump mode: $dump"
fancyEcho "Verbose mode: $verbose"
fancyEcho "Silent mode: $silent"

if [[ "$dump" == true ]]; then
    command="./gradlew validatePublishedArtifacts --dump"
else
    command="./gradlew validatePublishedArtifacts"
fi

failed=()

function validate() {
    if [[ "$verbose" == true && "$silent" != true ]]; then
        full="$command publish$1ToBuildRepoRepository --info --stacktrace"
    else
        full="$command publish$1ToBuildRepoRepository"
    fi

    if [[ "$silent" == true ]]; then
        full="$full -q --console=plain"
    fi

    if bash -c "$full"; then
        status="$?"
        if [[ "$status" != 1 && "$status" != 0 ]]; then
          exit "$status"
        fi

        fancyEcho "$1 SUCCESS"
    else
        status="$?"
        if [[ "$status" != 1 && "$status" != 0 ]]; then
          exit "$status"
        fi

        failed+=("$1")
        fancyEcho "$1 FAIL"
        fancyEcho "To reproduce - run:"
        fancyEcho "$full"
    fi;
}

validate JsPublication
validate WasmJsPublication
validate JvmPublication
validate BomPublication
validate MavenPublication
validate KotlinMultiplatformPublication
validate ApplePublication
validate WindowsPublication
validate LinuxPublication

fancyEcho ""
fancyEcho "-----------------------------------------------------------"
fancyEcho ""
fancyEcho "Validation Finished"

if [[ "${#failed[@]}" -eq 0 ]]; then
    fancyEcho "All tasks successful"
else
    fancyEcho "Failed tasks:"
    for i in "${failed[@]}" ; do
        fancyEcho "  - $i"
    done
    exit 1
fi
