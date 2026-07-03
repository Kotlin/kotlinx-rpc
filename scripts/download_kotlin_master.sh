#!/bin/bash

#
# Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#

# download_kotlin_master.sh
#
# Downloads the latest successful Kotlin master (public) compiler artifacts from the
# JetBrains TeamCity aggregate build and unzips them into ./lib-kotlin, for testing
# kotlinx-rpc against Kotlin master.
#
# Usage:
#   BUILD_SERVER_TOKEN=<token> ./scripts/download_kotlin_master.sh
#
# The token is read from $BUILD_SERVER_TOKEN, falling back to `buildserver.token` in
# ~/.gradle/gradle.properties. If $KOTLIN_MASTER_VERSION_FILE is set, the detected
# Kotlin version is written there for the caller.
#
# This is the shared "core" download logic; the TeamCity master builds
# (downloadMasterArtifactsAndSetKotlinVersion() in the kotlinx-rpc-build repo)
# call it and add their CI-specific behavior on top. Run from the repository root.

set -euo pipefail

if [[ -z ${BUILD_SERVER_TOKEN:-} ]]; then
  BUILD_SERVER_TOKEN=$(sed -En 's/buildserver.token=(.*)/\1/p' "$HOME"/.gradle/gradle.properties 2>/dev/null || true)

  if [[ -z $BUILD_SERVER_TOKEN ]]; then
    echo "No BUILD_SERVER_TOKEN token present"
    exit 1
  fi;
fi;

# Artifacts from https://teamcity.jetbrains.com/buildConfiguration/Kotlin_KotlinPublic_Artifacts_aggregate
response=$(curl -f "https://teamcity.jetbrains.com/app/rest/builds?locator=count:1,buildType:Kotlin_KotlinPublic_Artifacts_aggregate,status:SUCCESS,state:finished" --request GET --header "Authorization: Bearer $BUILD_SERVER_TOKEN")

buildId=$(echo "$response" | sed -En 's/.*build id="([0-9]+)".*/\1/p')
echo "Build id: $buildId"

version=$(echo "$response" | sed -En 's/.*number="([0-9a-zA-Z\.-]+)".*/\1/p')
echo "Build Kotlin version: $version"

# Optional hand-off for callers (e.g. the TeamCity wrapper) that need the detected version.
if [[ -n "${KOTLIN_MASTER_VERSION_FILE:-}" ]]; then
  echo "$version" > "$KOTLIN_MASTER_VERSION_FILE"
fi

curl -f "https://teamcity.jetbrains.com/app/rest/builds/id:$buildId/artifacts/content/maven.zip" --request GET --header "Authorization: Bearer $BUILD_SERVER_TOKEN" --output maven.zip

unzip maven.zip -d lib-kotlin

rm maven.zip

# K/N is not needed so far, but just in case, here is the code for it:
#
# kn=kotlin-native-prebuilt-linux-x86_64-$version
# curl -f "https://teamcity.jetbrains.com/app/rest/builds/id:$buildId/artifacts/content/$kn.tar.gz" --request GET --header "Authorization: Bearer $BUILD_SERVER_TOKEN" --output "$kn.tar.gz"
# tar -xzf "$kn.tar.gz"
# mv "$kn" ~/.konan/"$kn"
