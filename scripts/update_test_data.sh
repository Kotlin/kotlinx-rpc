#!/bin/bash

#
# Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#

# update_test_data.sh
#
# Regenerates golden test data for the compiler-plugin box/diagnostic tests by running
# :tests:compiler-plugin-tests with -Pkotlin.test.update.test.data=true.
#
# Usage:
#   ./scripts/update_test_data.sh <TestRunner> [testMethod]
# Examples:
#   ./scripts/update_test_data.sh BoxTest                # all box test golden files
#   ./scripts/update_test_data.sh DiagnosticTest         # all diagnostic test golden files
#   ./scripts/update_test_data.sh BoxTest myTestName     # a single test's golden file
#
# Pass the test class name without its package and without the "Generated" suffix.
# Run from the repository root.

set -euo pipefail

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
