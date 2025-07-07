#!/bin/bash

#
# Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#

if [ "$#" -ne 1 ] && [ "$#" -ne 2 ]; then
  echo "Pass test name without the package and the 'Generated' suffix, for example:" >&2
  echo "" >&2
  echo "$0 BoxTest" >&2
  exit 1
fi;

if [ "$#" -eq 2 ]; then
  TEST_NAME=".$2"
else
  TEST_NAME=""
fi;

set -o xtrace

./gradlew \
    :tests:compiler-plugin-tests:test \
    --tests "kotlinx.rpc.codegen.test.runners.$1Generated$TEST_NAME" \
    --continue \
    --stacktrace \
    -Pkotlin.test.update.test.data=true
