/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import util.findKonanHome
import util.konanHomeProvider
import util.registerCheckKonanHomeTask
import util.registerPrepareKonanHomeTask

plugins {
    alias(libs.plugins.conventions.jvm)
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}

val nativeCompilerEmbeddable = file(
    findKonanHome() + "/konan/lib/kotlin-native-compiler-embeddable.jar",
)

val konanHome = konanHomeProvider()
val prepareKonanHome = registerPrepareKonanHomeTask(
    downloadTaskPath = ":grpc:grpc-core:downloadKotlinNativeDistribution",
)
val checkKonanHome = registerCheckKonanHomeTask(
    prepareKonanHome = prepareKonanHome,
    konanHome = konanHome,
)

dependencies {
    implementation(files(nativeCompilerEmbeddable))
}

tasks.compileKotlin {
    dependsOn(checkKonanHome)
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from({
        configurations.compileClasspath.get()
            .filter { it.name.endsWith(".jar") }
            .map(::zipTree)
    })
}
