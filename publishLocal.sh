#!/bin/bash

#
# Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#

set -euxo pipefail

./gradlew publishAllPublicationsToBuildRepoRepository
./gradlew -p compiler-plugin publishAllPublicationsToBuildRepoRepository
./gradlew -p gradle-plugin publishAllPublicationsToBuildRepoRepository

if [ -x "./gradlew -p ksp-plugin publishAllPublicationsToBuildRepoRepository" ]; then
  echo "KSP is on"
else
  echo "KSP is off"
fi;
