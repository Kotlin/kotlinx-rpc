#!/bin/bash

#
# Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#

set -e

if [[ -z $BUILD_SERVER_TOKEN ]]; then
  BUILD_SERVER_TOKEN=$(cat "$HOME"/.gradle/gradle.properties | sed -En 's/buildserver.token=(.*)/\1/p')

  if [[ -z $BUILD_SERVER_TOKEN ]]; then
    echo "No BUILD_SERVER_TOKEN token present"
    exit 1
  fi;
fi;

# Artifacts from https://teamcity.jetbrains.com/buildConfiguration/Kotlin_KotlinPublic_Artifacts
response=$(curl -f "https://teamcity.jetbrains.com/app/rest/builds?locator=count:1,buildType:Kotlin_KotlinPublic_Artifacts,status:SUCCESS,state:finished" --request GET --header "Authorization: Bearer $BUILD_SERVER_TOKEN")

buildId=$(echo "$response" | sed -En 's/.*build id="([0-9]+)".*/\1/p')
echo "Build id: $buildId"

version=$(echo "$response" | sed -En 's/.*number="([0-9a-zA-Z\.-]+)".*/\1/p')
echo "Build Kotlin version: $version"

curl -f "https://teamcity.jetbrains.com/app/rest/builds/id:$buildId/artifacts/content/maven.zip" --request GET --header "Authorization: Bearer $BUILD_SERVER_TOKEN" --output maven.zip

unzip maven.zip -d lib-kotlin

rm maven.zip
