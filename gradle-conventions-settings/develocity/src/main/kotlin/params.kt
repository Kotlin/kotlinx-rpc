/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

const val DEVELOCITY_SERVER = "https://ge.jetbrains.com"
const val GITHUB_REPO = "https://github.com/Kotlin/kotlinx-rpc"
const val TEAMCITY_URL = "https://krpc.teamcity.com"

// isCIRun is computed in conventions-develocity.settings.gradle.kts using the CC-safe
// providers.environmentVariable() API and passed explicitly to functions that need it.
// Previously this was a top-level val using System.getenv(), which is not tracked by the
// configuration cache — the value was baked in and never re-evaluated across builds.
