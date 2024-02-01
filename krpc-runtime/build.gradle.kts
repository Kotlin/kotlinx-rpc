/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest

/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.krpc)
    alias(libs.plugins.atomicfu)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":krpc-runtime:krpc-runtime-api"))

                implementation(project(":krpc-utils"))
                implementation(project(":krpc-utils:krpc-utils-service-loader"))
                implementation(project(":krpc-runtime:krpc-runtime-serialization"))

                api(libs.coroutines.core)
                implementation(libs.serialization.core)
                implementation(libs.kotlin.reflect)

                implementation(project(":krpc-runtime::krpc-runtime-logging"))
            }
        }
        commonTest {
            dependencies {
                implementation(project(":krpc-runtime:krpc-runtime-client"))
                implementation(project(":krpc-runtime:krpc-runtime-server"))
                implementation(project(":krpc-runtime:krpc-runtime-serialization"))

                implementation(libs.kotlin.test)
                implementation(libs.coroutines.test)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.slf4j.api)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(project(":krpc-runtime:krpc-runtime-test"))
                implementation(project(":krpc-runtime:krpc-runtime-serialization:krpc-runtime-serialization-json"))
                implementation(project(":krpc-runtime:krpc-runtime-serialization:krpc-runtime-serialization-cbor"))
                implementation(project(":krpc-runtime:krpc-runtime-serialization:krpc-runtime-serialization-protobuf"))

                implementation(libs.slf4j.simple)
            }
        }
    }
}

tasks.withType<KotlinJvmTest> {
    environment("LIBRARY_VERSION", libs.versions.krpc.core.get())
}

tasks.named("clean") {
    doLast {
        projectDir.resolve("src/jvmTest/resources").walk().forEach {
            if (it.isFile && it.extension == "tmp") {
                it.delete()
            }
        }
    }
}

evaluationDependsOn(":krpc-runtime:krpc-runtime-test")

// otherwise it complains and fails the build on 1.8.*
val jsProductionLibraryCompileSync: TaskProvider<Task> = project(":krpc-runtime:krpc-runtime-test")
    .tasks.named("jsProductionLibraryCompileSync")

tasks.named("jsBrowserTest") {
    dependsOn(jsProductionLibraryCompileSync)
}

tasks.named("jsNodeTest") {
    dependsOn(jsProductionLibraryCompileSync)
}
