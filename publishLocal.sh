#!/bin/bash

#
# Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#

set -euxo pipefail

./gradlew publishAllPublicationsToBuildRepoRepository \
  :compiler-plugin:publishAllPublicationsToBuildRepoRepository \
  :ksp-plugin:publishAllPublicationsToBuildRepoRepository \
  :gradle-plugin:publishAllPublicationsToBuildRepoRepository
