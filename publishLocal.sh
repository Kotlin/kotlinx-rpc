#!/bin/bash

#
# Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#

set -euxo pipefail

./gradlew publishAllPublicationsToBuildRepoRepository
./gradlew -p compiler-plugin publishAllPublicationsToBuildRepoRepository --no-configuration-cache
./gradlew -p gradle-plugin publishAllPublicationsToBuildRepoRepository --no-configuration-cache
./gradlew -p protoc-gen publishAllPublicationsToBuildRepoRepository --no-configuration-cache
