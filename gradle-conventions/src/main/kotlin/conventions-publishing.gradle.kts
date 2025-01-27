/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.kotlin.dsl.registering
import util.*
import util.other.capitalized
import util.other.getSensitiveProperty
import util.other.isPublicModule
import util.tasks.ValidatePublishedArtifactsTask

val isGradlePlugin = project.name == "gradle-plugin"
val publishingExtension = project.extensions.findByType<PublishingExtension>()
val globalRootDir: String by extra

if (isPublicModule) {
    if (publishingExtension == null) {
        apply(plugin = "maven-publish")
    }

    if (project.getSensitiveProperty("libs.sign.key.private") != null) {
        apply(plugin = "signing")
    }

    the<PublishingExtension>().apply {
        configurePublication()

        project.withKotlinKmpExtension {
            // Remove then first Jvm Only public module is created
            val publishMavenPublication = "publishMavenPublication"
            repositories.all {
                val publishTaskName = "${publishMavenPublication}To${name.capitalized()}Repository"
                if (tasks.findByName(publishTaskName) == null) {
                    tasks.register(publishTaskName) {
                        group = PublishingPlugin.PUBLISH_TASK_GROUP
                    }
                }
            }
        }
    }

    logger.info("Configured ${project.name} for publication")
} else {
    logger.info("Skipping ${project.name} publication configuration, not a public module")
}

fun PublishingExtension.configurePublication() {
    repositories {
        configureSpaceEapRepository()
        configureSpaceGrpcRepository()
        configureForIdeRepository()
        configureLocalDevRepository()
    }

    configureJvmPublicationIfNeeded()

    val javadocJar = if (!isGradlePlugin) {
        configureEmptyJavadocArtifact()
    } else {
        null
    }

    publications.withType(MavenPublication::class).all {
        pom.configureMavenCentralMetadata()
        signPublicationIfKeyPresent()
        if (javadocJar != null) {
            artifact(javadocJar)
        }

        // mainly for kotlinMultiplatform publication
        setPublicArtifactId(project)

        if (!isGradlePlugin) {
            fixModuleMetadata(project)
        }

        logger.info("Project ${project.name} -> Publication configured: $name, $version")
    }
}

// we need to configure maven publication for kotlin("jvm") projects manually
fun PublishingExtension.configureJvmPublicationIfNeeded() {
    if (isGradlePlugin) {
        return
    }

    project.withKotlinJvmExtension {
        if (publications.isNotEmpty()) {
            return@withKotlinJvmExtension
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

fun RepositoryHandler.configureSpaceEapRepository() {
    configureRepository(project) {
        username = "SPACE_USERNAME"
        password = "SPACE_PASSWORD"
        name = "space"
        url = "https://maven.pkg.jetbrains.space/public/p/krpc/eap"
    }
}

fun RepositoryHandler.configureSpaceGrpcRepository() {
    configureRepository(project) {
        username = "SPACE_USERNAME"
        password = "SPACE_PASSWORD"
        name = "grpc"
        url = "https://maven.pkg.jetbrains.space/public/p/krpc/grpc"
    }
}

fun RepositoryHandler.configureForIdeRepository() {
    configureRepository(project) {
        username = "SPACE_USERNAME"
        password = "SPACE_PASSWORD"
        name = "forIde"
        url = "https://maven.pkg.jetbrains.space/public/p/krpc/for-ide"
    }
}

fun RepositoryHandler.configureLocalDevRepository() {
    // Something that's straightforward to "clean" for development, not mavenLocal
    maven("$globalRootDir/build/repo") {
        name = "buildRepo"
    }
}

fun configureEmptyJavadocArtifact(): TaskProvider<Jar?> {
    val javadocJar by project.tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
        // contents are deliberately left empty
        // https://central.sonatype.org/publish/requirements/#supply-javadoc-and-sources
    }
    return javadocJar
}

fun MavenPublication.signPublicationIfKeyPresent() {
    if (gradle.startParameter.taskNames.contains(ValidatePublishedArtifactsTask.NAME)) {
        return
    }

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
