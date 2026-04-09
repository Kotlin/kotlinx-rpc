/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(InternalRpcApi::class)

import kotlinx.rpc.internal.InternalRpcApi
import kotlinx.rpc.internal.configureLocalProtocGenDevelopmentDependency
import kotlinx.rpc.protoc.buf
import kotlinx.rpc.protoc.generate
import kotlinx.rpc.protoc.protoTasks
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import util.other.localProperties
import util.tasks.CONFORMANCE_PB
import util.tasks.GenerateConformanceFileDescriptorSet
import util.tasks.PAYLOAD_PB
import util.tasks.setupProtobufConformanceResources

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled

    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.executable {
            entryPoint = "kotlinx.rpc.protoc.gen.test.main"
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.coroutines.core)
                implementation(projects.grpc.grpcMarshaller)
                implementation(projects.protobuf.protobufCore)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.kotlin.reflect)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.kotlin.test.junit5)
                implementation(libs.coroutines.test)
            }
        }
    }
}

setupProtobufConformanceResources()
configureLocalProtocGenDevelopmentDependency("CommonMain")

fun generatedCodeDir(sourceSetName: String): File = project.layout.projectDirectory
    .dir("src")
    .dir(sourceSetName)
    .dir("generated-code")
    .asFile

rpc {
    protoc {
        buf.generate.comments {
            includeFileLevelComments = false
        }
    }
}

protoTasks.buf.generate.nonTestTasks().configureEach {
    outputDirectory = generatedCodeDir(properties.sourceSetNames.single())
}

// Use lazy configuration references to avoid premature resolution of jvmRuntimeClasspath
val jvmRuntimeClasspath = configurations.named("jvmRuntimeClasspath")

val mockClientJar = tasks.register<Jar>("mockClientJar") {
    archiveBaseName.set("mockClient")
    archiveVersion.set("")

    manifest {
        attributes["Main-Class"] = "kotlinx.rpc.protoc.gen.test.ConformanceClientKt"
    }

    dependsOn(tasks.named("jvmMainClasses"))

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(
        jvmRuntimeClasspath.map { config ->
            config.map { if (it.isDirectory()) it else zipTree(it) }
        }
    )
    from(tasks.named("compileKotlinJvm").map { it.outputs.files })
    from(tasks.named("jvmProcessResources").map { it.outputs.files })
}

val generateConformanceTests = tasks.register<JavaExec>("generateConformanceTests") {
    classpath = files(jvmRuntimeClasspath, tasks.named("compileKotlinJvm").map { it.outputs.files })

    dependsOn(mockClientJar)
    dependsOn(protoTasks.buf.generate.matchingSourceSet("commonMain"))
    dependsOn(tasks.named("jvmMainClasses"))

    args = listOf(
        mockClientJar.get().archiveFile.get().asFile.absolutePath
    )

    mainClass.set("kotlinx.rpc.protoc.gen.test.GenerateConformanceTestsKt")
}

val conformanceTest = properties.getOrDefault("conformance.test", "").toString()
val conformanceTestDebug =
    properties.getOrDefault("conformance.test.debug", "false").toString().toBooleanStrictOrNull() ?: false

val generateConformanceFileDescriptorSet = tasks
    .withType<GenerateConformanceFileDescriptorSet>()

tasks.register<JavaExec>("runConformanceTest") {
    classpath = files(jvmRuntimeClasspath, tasks.named("compileKotlinJvm").map { it.outputs.files })

    dependsOn(mockClientJar)
    dependsOn(protoTasks.buf.generate.matchingSourceSet("commonMain"))
    dependsOn(generateConformanceFileDescriptorSet)
    dependsOn(tasks.named("jvmMainClasses"))

    args = listOfNotNull(
        mockClientJar.get().archiveFile.get().asFile.absolutePath,
        conformanceTest,
        if (conformanceTestDebug) "--debug" else null
    )

    mainClass.set("kotlinx.rpc.protoc.gen.test.RunConformanceTestKt")

    val protoscope = localProperties().getProperty("protoscope_path")

    environment("PROTOSCOPE_PATH", protoscope)

    val pbFiles = generateConformanceFileDescriptorSet.map {
        it.outputFile.get()
    }

    environment(
        "CONFORMANCE_PB_PATH",
        pbFiles.single { it.name == CONFORMANCE_PB }.absolutePath
    )
    environment(
        "TEST_ALL_TYPES_PROTO3_PB_PATH",
        pbFiles.single { it.name == PAYLOAD_PB }.absolutePath
    )

    doFirst {
        if (protoscope == null || !File(protoscope).exists()) {
            throw GradleException("protoscope_path property is not set. Run ./setup_protoscope.sh")
        }
    }
}

tasks.named<Test>("jvmTest") {
    environment("MOCK_CLIENT_JAR", mockClientJar.get().archiveFile.get().asFile.absolutePath)

    useJUnitPlatform()

    dependsOn(generateConformanceTests)
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    enabled = false
}
