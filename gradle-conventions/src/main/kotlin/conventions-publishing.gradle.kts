/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.*

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

    // separate function is needed for different gradle versions
    // in 7.6 `Configuration` argument is `this`, in 8.* it is a first argument (hence `it`)
    val onPublication: (MavenPublication) -> Unit = { publication ->
        publication.pom.configureMavenCentralMetadata()
        publication.signPublicationIfKeyPresent()
        if (javadocJar != null) {
            publication.artifact(javadocJar)
        }

        publication.setPublicArtifactId(project)

        if (!isGradlePlugin) {
            publication.fixModuleMetadata(project)
        }

        logger.info("Project ${project.name} -> Publication configured: ${publication.name}")
    }

    publications.withType(MavenPublication::class).all(onPublication)

    tasks.withType<PublishToMavenRepository>().configureEach {
        dependsOn(tasks.withType<Sign>())
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
