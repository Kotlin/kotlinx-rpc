#!/usr/bin/env bash
#
# Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#

set -Eeuo pipefail
trap 'echo "ERROR: Build failed at ${BASH_SOURCE}:${LINENO}" >&2' ERR

# Builds a static (fat) library dependency (e.g. protobuf or grpc) and publishes to the space file registry.
#
# Usage:
#   ./build_and_publish_dep.sh <bazel_target> <out_dir> <platform> <short> <os>
# Example:
#   ./build_and_publish_dep.sh @com_github_protobuffers_protobuf//:protobuf_lite_static out @build_bazel_apple_support//platforms:ios_arm64 ios_arm64 ios
#
# The example will produce ./out/com_github_protobuffers_protobuf/v31.1/libprotobuf_lite_fat.ios_arm64.a

LABEL="${1:?need bazel target label}"
OUTDIR="${2:?need output directory}"
PLATFORM="${3:?need a platform for bazel build command}"
SHORT="${4:?need the short name of the target}"
OS="${4:?need the operating system of the target platform}"

