/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.*

val isGradlePlugin = project.properties["kotlinx.rpc.gradle.plugin"] == "true"
val publishingExtension = project.extensions.findByType<PublishingExtension>()
val globalRootDir: String by extra

if (isPublicModule) {
    if (publishingExtension == null) {
        apply(plugin = "maven-publish")
    }

    if (project.getSensitiveProperty("libs.sign.key.private") != null) {
        apply(plugin = "signing")
    }

    the<PublishingExtension>().configurePublication()
    logger.info("Configured ${project.name} for publication")
} else {
    logger.info("Skipping ${project.name} publication configuration, not a public module")
}

fun PublishingExtension.configurePublication() {
    repositories {
        configureSonatypeRepository()
        configureSpaceRepository()
        configureLocalDevRepository()
    }

    configureJvmPublicationIfNeeded()

    val javadocJar = if (!isGradlePlugin) {
        configureEmptyJavadocArtifact()
    } else {
        null
    }

    val remove = mutableSetOf<MavenPublication>()

    // separate function is needed for different gradle versions
    // in 7.6 `Configuration` argument is `this`, in 8.* it is a first argument (hence `it`)
    val onPublication: (MavenPublication) -> Unit = { publication ->
        publication.pom.configureMavenCentralMetadata()
        publication.signPublicationIfKeyPresent()
        if (javadocJar != null) {
            publication.artifact(javadocJar)
        }

        publication.setPublicArtifactId(project)

        val suffix = when (publication.name) {
            "kotlinMultiplatform" -> ""
            else -> "-${publication.name.lowercase()}"
        }

        print("test publication: ${publication.artifactId + suffix} -> ")
        if (publication.artifactId + suffix !in missing) {
            remove.add(publication)
            println("REJECT")
        } else {
            println("OK")
        }

        if (!isGradlePlugin) {
            publication.fixModuleMetadata(project)
        }

        logger.info("Project ${project.name} -> Publication configured: ${publication.name}")
    }

    publications.withType(MavenPublication::class).all(onPublication)
    publications.removeAll(remove)

    tasks.withType<PublishToMavenRepository>().configureEach {
        dependsOn(tasks.withType<Sign>())
    }
}

// we need to configure maven publication for kotlin("jvm") projects manually
fun PublishingExtension.configureJvmPublicationIfNeeded() {
    if (isGradlePlugin) {
        return
    }

    project.plugins.withId("org.jetbrains.kotlin.jvm") {
        if (publications.isNotEmpty()) {
            return@withId
        }

        logger.info("Manually added maven publication to ${project.name}")
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
    }
}

fun MavenPom.configureMavenCentralMetadata() {
    name by project.name
    description by "kotlinx.rpc, a Kotlin library for adding asynchronous RPC services to your applications."
    url by "https://github.com/Kotlin/kotlinx-rpc"

    licenses {
        license {
            name by "The Apache Software License, Version 2.0"
            url by "https://www.apache.org/licenses/LICENSE-2.0.txt"
            distribution by "repo"
        }
    }

    developers {
        developer {
            id by "JetBrains"
            name by "JetBrains Team"
            organization by "JetBrains"
            organizationUrl by "https://www.jetbrains.com"
        }
    }

    scm {
        url by "https://github.com/Kotlin/kotlinx-rpc"
        connection by "scm:git:git://github.com/Kotlin/kotlinx-rpc.git"
        developerConnection by "scm:git:git@github.com:Kotlin/kotlinx-rpc.git"
    }
}

fun RepositoryHandler.configureSpaceRepository() {
    configureRepository(project) {
        username = "SPACE_USERNAME"
        password = "SPACE_PASSWORD"
        name = "space"
        url = "https://maven.pkg.jetbrains.space/public/p/krpc/maven"
    }
}

fun RepositoryHandler.configureLocalDevRepository() {
    // Something that's straightforward to "clean" for development, not mavenLocal
    maven("$globalRootDir/build/repo") {
        name = "buildRepo"
    }
}

fun RepositoryHandler.configureSonatypeRepository() {
    configureRepository(project) {
        username = "libs.sonatype.user"
        password = "libs.sonatype.password"
        name = "sonatype"
        url = sonatypeRepositoryUri
    }
}

val sonatypeRepositoryUri: String?
    get() {
        val repositoryId: String = project.getSensitiveProperty("libs.repository.id")
            ?.takeIf { it.isNotBlank() }
            ?: return null

        return "https://oss.sonatype.org/service/local/staging/deployByRepositoryId/$repositoryId"
    }

fun configureEmptyJavadocArtifact(): org.gradle.jvm.tasks.Jar {
    val javadocJar by project.tasks.creating(Jar::class) {
        archiveClassifier.set("javadoc")
        // contents are deliberately left empty
        // https://central.sonatype.org/publish/requirements/#supply-javadoc-and-sources
    }
    return javadocJar
}

fun MavenPublication.signPublicationIfKeyPresent() {
    val keyId = project.getSensitiveProperty("libs.sign.key.id")
    val signingKey = project.getSensitiveProperty("libs.sign.key.private")
    val signingKeyPassphrase = project.getSensitiveProperty("libs.sign.passphrase")

    if (!signingKey.isNullOrBlank()) {
        the<SigningExtension>().apply {
            useInMemoryPgpKeys(keyId, signingKey, signingKeyPassphrase)

            sign(this@signPublicationIfKeyPresent)
        }
    }
}

val missing = setOf(
    "kotlinx-rpc-krpc-logging-linuxx64",
    "kotlinx-rpc-krpc-logging-macosarm64",
    "kotlinx-rpc-krpc-logging-macosx64",
    "kotlinx-rpc-krpc-logging-tvosarm64",
    "kotlinx-rpc-krpc-logging-tvosx64",
    "kotlinx-rpc-krpc-logging-watchosarm64",
    "kotlinx-rpc-krpc-logging-watchossimulatorarm64",
    "kotlinx-rpc-krpc-logging-watchosx64",
    "kotlinx-rpc-krpc-serialization-cbor",
    "kotlinx-rpc-krpc-serialization-cbor-iosarm64",
    "kotlinx-rpc-krpc-serialization-cbor-iossimulatorarm64",
    "kotlinx-rpc-krpc-serialization-cbor-iosx64",
    "kotlinx-rpc-krpc-serialization-cbor-js",
    "kotlinx-rpc-krpc-serialization-cbor-jvm",
    "kotlinx-rpc-krpc-serialization-cbor-linuxarm64",
    "kotlinx-rpc-krpc-serialization-cbor-linuxx64",
    "kotlinx-rpc-krpc-serialization-cbor-macosarm64",
    "kotlinx-rpc-krpc-serialization-cbor-macosx64",
    "kotlinx-rpc-krpc-serialization-cbor-tvosarm64",
    "kotlinx-rpc-krpc-serialization-cbor-tvosx64",
    "kotlinx-rpc-krpc-serialization-cbor-watchosarm64",
    "kotlinx-rpc-krpc-serialization-cbor-watchossimulatorarm64",
    "kotlinx-rpc-krpc-serialization-cbor-watchosx64",
    "kotlinx-rpc-krpc-serialization-core",
    "kotlinx-rpc-krpc-serialization-core-iosarm64",
    "kotlinx-rpc-krpc-serialization-core-iossimulatorarm64",
    "kotlinx-rpc-krpc-serialization-core-iosx64",
    "kotlinx-rpc-krpc-serialization-core-js",
    "kotlinx-rpc-krpc-serialization-core-jvm",
    "kotlinx-rpc-krpc-serialization-core-linuxarm64",
    "kotlinx-rpc-krpc-serialization-core-linuxx64",
    "kotlinx-rpc-krpc-serialization-core-macosarm64",
    "kotlinx-rpc-krpc-serialization-core-macosx64",
    "kotlinx-rpc-krpc-serialization-core-tvosarm64",
    "kotlinx-rpc-krpc-serialization-core-tvosx64",
    "kotlinx-rpc-krpc-serialization-core-watchosarm64",
    "kotlinx-rpc-krpc-serialization-core-watchossimulatorarm64",
    "kotlinx-rpc-krpc-serialization-core-watchosx64",
    "kotlinx-rpc-krpc-serialization-json",
    "kotlinx-rpc-krpc-serialization-json-iosarm64",
    "kotlinx-rpc-krpc-serialization-json-iossimulatorarm64",
    "kotlinx-rpc-krpc-serialization-json-iosx64",
    "kotlinx-rpc-krpc-serialization-json-js",
    "kotlinx-rpc-krpc-serialization-json-jvm",
    "kotlinx-rpc-krpc-serialization-json-linuxarm64",
    "kotlinx-rpc-krpc-serialization-json-linuxx64",
    "kotlinx-rpc-krpc-serialization-json-macosarm64",
    "kotlinx-rpc-krpc-serialization-json-macosx64",
    "kotlinx-rpc-krpc-serialization-json-tvosarm64",
    "kotlinx-rpc-krpc-serialization-json-tvosx64",
    "kotlinx-rpc-krpc-serialization-json-watchosarm64",
    "kotlinx-rpc-krpc-serialization-json-watchossimulatorarm64",
    "kotlinx-rpc-krpc-serialization-json-watchosx64",
    "kotlinx-rpc-krpc-serialization-protobuf",
    "kotlinx-rpc-krpc-serialization-protobuf-iosarm64",
    "kotlinx-rpc-krpc-serialization-protobuf-iossimulatorarm64",
    "kotlinx-rpc-krpc-serialization-protobuf-iosx64",
    "kotlinx-rpc-krpc-serialization-protobuf-js",
    "kotlinx-rpc-krpc-serialization-protobuf-jvm",
    "kotlinx-rpc-krpc-serialization-protobuf-linuxarm64",
    "kotlinx-rpc-krpc-serialization-protobuf-linuxx64",
    "kotlinx-rpc-krpc-serialization-protobuf-macosarm64",
    "kotlinx-rpc-krpc-serialization-protobuf-macosx64",
    "kotlinx-rpc-krpc-serialization-protobuf-tvosarm64",
    "kotlinx-rpc-krpc-serialization-protobuf-tvosx64",
    "kotlinx-rpc-krpc-serialization-protobuf-watchosarm64",
    "kotlinx-rpc-krpc-serialization-protobuf-watchossimulatorarm64",
    "kotlinx-rpc-krpc-serialization-protobuf-watchosx64",
    "kotlinx-rpc-krpc-server",
    "kotlinx-rpc-krpc-server-iosarm64",
    "kotlinx-rpc-krpc-server-iossimulatorarm64",
    "kotlinx-rpc-krpc-server-iosx64",
    "kotlinx-rpc-krpc-server-js",
    "kotlinx-rpc-krpc-server-jvm",
    "kotlinx-rpc-krpc-server-linuxarm64",
    "kotlinx-rpc-krpc-server-linuxx64",
    "kotlinx-rpc-krpc-server-macosarm64",
    "kotlinx-rpc-krpc-server-macosx64",
    "kotlinx-rpc-krpc-server-tvosarm64",
    "kotlinx-rpc-krpc-server-tvosx64",
    "kotlinx-rpc-krpc-server-watchosarm64",
    "kotlinx-rpc-krpc-server-watchossimulatorarm64",
    "kotlinx-rpc-krpc-server-watchosx64",
    "kotlinx-rpc-krpc-test",
    "kotlinx-rpc-krpc-test-iosarm64",
    "kotlinx-rpc-krpc-test-iossimulatorarm64",
    "kotlinx-rpc-krpc-test-iosx64",
    "kotlinx-rpc-krpc-test-js",
    "kotlinx-rpc-krpc-test-jvm",
    "kotlinx-rpc-krpc-test-linuxarm64",
    "kotlinx-rpc-krpc-test-linuxx64",
    "kotlinx-rpc-krpc-test-macosarm64",
    "kotlinx-rpc-krpc-test-macosx64",
    "kotlinx-rpc-krpc-test-tvosarm64",
    "kotlinx-rpc-krpc-test-tvosx64",
    "kotlinx-rpc-krpc-test-watchosarm64",
    "kotlinx-rpc-krpc-test-watchossimulatorarm64",
    "kotlinx-rpc-krpc-test-watchosx64",
    "kotlinx-rpc-utils",
    "kotlinx-rpc-utils-iosarm64",
    "kotlinx-rpc-utils-iossimulatorarm64",
    "kotlinx-rpc-utils-iosx64",
    "kotlinx-rpc-utils-js",
    "kotlinx-rpc-utils-jvm",
    "kotlinx-rpc-utils-linuxarm64",
    "kotlinx-rpc-utils-linuxx64",
    "kotlinx-rpc-utils-macosarm64",
    "kotlinx-rpc-utils-macosx64",
    "kotlinx-rpc-utils-tvosarm64",
    "kotlinx-rpc-utils-tvosx64",
    "kotlinx-rpc-utils-watchosarm64",
    "kotlinx-rpc-utils-watchossimulatorarm64",
    "kotlinx-rpc-utils-watchosx64"
)
