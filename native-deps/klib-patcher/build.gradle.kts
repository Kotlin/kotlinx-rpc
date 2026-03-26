/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.findKonanHome
import util.konanHomeProvider
import util.registerCheckKonanHomeTask
import util.registerPrepareKonanHomeTask

plugins {
    // Keep this tool on javac. Compiling even a tiny Kotlin source set against the full
    // kotlin-native-compiler-embeddable classpath was slow and hit heap OOMs in practice.
    alias(libs.plugins.conventions.common)
    java
}

java {
    withSourcesJar()
}

val nativeDepsKonanDownloadTaskPath = providers.gradleProperty("nativeDepsKonanDownloadTaskPath").orNull
    ?: error("Missing nativeDepsKonanDownloadTaskPath in ${rootProject.layout.projectDirectory.file("gradle.properties").asFile}")

val nativeCompilerEmbeddable = file(
    findKonanHome() + "/konan/lib/kotlin-native-compiler-embeddable.jar",
)

val konanHome = konanHomeProvider()
val prepareKonanHome = registerPrepareKonanHomeTask(
    downloadTaskPath = nativeDepsKonanDownloadTaskPath,
)
val checkKonanHome = registerCheckKonanHomeTask(
    prepareKonanHome = prepareKonanHome,
    konanHome = konanHome,
)

dependencies {
    compileOnly(files(nativeCompilerEmbeddable))
}

tasks.compileJava {
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
