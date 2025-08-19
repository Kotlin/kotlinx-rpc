/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(InternalRpcApi::class)

import kotlinx.rpc.buf.tasks.BufGenerateTask
import kotlinx.rpc.internal.InternalRpcApi
import kotlinx.rpc.internal.configureLocalProtocGenDevelopmentDependency
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import util.other.localProperties
import util.tasks.CONFORMANCE_PB
import util.tasks.GenerateConformanceFileDescriptorSet
import util.tasks.PAYLOAD_PB
import util.tasks.setupProtobufConformanceResources

plugins {
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.kotlinx.rpc)
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}

dependencies {
    implementation(libs.coroutines.core)
    implementation(libs.kotlin.reflect)
    implementation(projects.grpc.grpcCodec)
    implementation(projects.protobuf.protobufCore)

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.coroutines.test)
}

setupProtobufConformanceResources()
configureLocalProtocGenDevelopmentDependency("Main")

val generatedCodeDir = project.layout.projectDirectory
    .dir("src")
    .dir("main")
    .dir("generated-code")
    .asFile

tasks.withType<BufGenerateTask>().configureEach {
    if (name.endsWith("Main")) {
        outputDirectory = generatedCodeDir
    }
}

protoSourceSets {
    main {
        proto {
            exclude("**/test_messages_proto2.proto")
            exclude("**/test_messages_proto2_editions.proto")
            exclude("**/test_messages_edition2023.proto")
        }
    }
}

val mockClientJar = tasks.register<Jar>("mockClientJar") {
    archiveBaseName.set("mockClient")
    archiveVersion.set("")

    manifest {
        attributes["Main-Class"] = "kotlinx.rpc.protoc.gen.test.ConformanceClientKt"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(
        configurations.runtimeClasspath.map { prop ->
            prop.map { if (it.isDirectory()) it else zipTree(it) }
        }
    )

    with(tasks.jar.get())
}


val generateConformanceTests = tasks.register<JavaExec>("generateConformanceTests") {
    classpath = sourceSets.main.get().runtimeClasspath

    dependsOn(mockClientJar)
    dependsOn(tasks.named("bufGenerateMain"))

    args = listOf(
        mockClientJar.get().archiveFile.get().asFile.absolutePath
    )

    mainClass.set("kotlinx.rpc.protoc.gen.test.GenerateConformanceTestsKt")
}

val conformanceTest = properties.getOrDefault("conformance.test", "").toString()
val conformanceTestDebug = properties.getOrDefault("conformance.test.debug", "false").toString().toBooleanStrictOrNull() ?: false

val generateConformanceFileDescriptorSet = tasks
    .withType<GenerateConformanceFileDescriptorSet>()

tasks.register<JavaExec>("runConformanceTest") {
    classpath = sourceSets.main.get().runtimeClasspath

    dependsOn(mockClientJar)
    dependsOn(tasks.named("bufGenerateMain"))
    dependsOn(generateConformanceFileDescriptorSet)

    args = listOfNotNull(
        mockClientJar.get().archiveFile.get().asFile.absolutePath,
        conformanceTest,
        if (conformanceTestDebug) "--debug" else null
    )

    mainClass.set("kotlinx.rpc.protoc.gen.test.RunConformanceTestKt")

    val protoscope = localProperties().getProperty("protoscope_path")
        ?: throw GradleException("protoscope_path property is not set. Run ./setup_protoscope.sh")

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
        if (!File(protoscope).exists()) {
            throw GradleException(
                """
                    Protoscope is not found. Use the following command to install it: 
                    
                    $ brew install go
                    $ go install github.com/protocolbuffers/protoscope/cmd/protoscope...@latest
                """.trimIndent()
            )
        }
    }
}

tasks.test {
    environment("MOCK_CLIENT_JAR", mockClientJar.get().archiveFile.get().asFile.absolutePath)

    useJUnitPlatform()

    dependsOn(generateConformanceTests)
}
