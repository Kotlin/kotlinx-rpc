#!/bin/bash

#
# Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#

# publish_local.sh
#
# Publishes all kotlinx-rpc artifacts to the local Maven repository at build/repo/ for
# local testing: the main library, the compiler plugin, the Gradle plugin, and protoc-gen.
#
# Usage:
#   ./scripts/publish_local.sh [extra gradle args...]
#
# Run from the repository root. Any extra arguments are forwarded to every Gradle invocation.

set -euxo pipefail

./gradlew publishAllPublicationsToBuildRepoRepository "$@"
./gradlew -p compiler-plugin publishAllPublicationsToBuildRepoRepository --no-configuration-cache "$@"
./gradlew -p gradle-plugin publishAllPublicationsToBuildRepoRepository --no-configuration-cache "$@"
./gradlew -p protoc-gen publishAllPublicationsToBuildRepoRepository --no-configuration-cache "$@"
