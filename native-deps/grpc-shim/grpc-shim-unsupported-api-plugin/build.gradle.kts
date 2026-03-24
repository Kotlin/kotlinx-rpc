/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import util.findKonanHome

plugins {
    alias(libs.plugins.conventions.jvm)
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}

val nativeCompilerEmbeddable = rootProject.file(
    rootProject.findKonanHome() + "/konan/lib/kotlin-native-compiler-embeddable.jar",
)

dependencies {
    implementation(files(nativeCompilerEmbeddable))
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from({
        configurations.compileClasspath.get()
            .filter { it.name.endsWith(".jar") }
            .map(::zipTree)
    })
}
