/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

rootProject.name = "gradle-settings-conventions"

// Code below is a hack because a chicken-egg problem, I can't use myself as a settings-plugin
apply(from="src/main/kotlin/settings-conventions.settings.gradle.kts")
