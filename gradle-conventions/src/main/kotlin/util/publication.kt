/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt.MaxLineLength")

package util

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.provider.Property
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.maven
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import util.other.getSensitiveProperty
import java.io.File

const val KOTLINX_RPC_PREFIX = "kotlinx-rpc"

/**
 * Important to configure inside [KotlinTarget.mavenPublication]
 * AND in [PublishingExtension.configurePublication] in the conventions-publishing.gradle.kts file.
 */
@Suppress("KDocUnresolvedReference")
fun MavenPublication.setPublicArtifactId(project: Project) {
    val publication = this

    if (!publication.artifactId.startsWith(KOTLINX_RPC_PREFIX)) {
        publication.artifactId = "$KOTLINX_RPC_PREFIX-$artifactId"
        project.logger.info("Altered artifactId for $name publication: $artifactId")
    }
}

/**
 * Fixes [KT-67965](https://youtrack.jetbrains.com/issue/KT-67965/KMP-root-publication-gradle-metadata-has-incorrect-GAV-coordinates-for-Apple-specific-targets-when-published-from-non-Apple)
 *
 * TLDR: module.json file of Gradle metadata does not always respect [setPublicArtifactId] change
 * So some properties are changed manually here.
 */
fun MavenPublication.fixModuleMetadata(project: Project) {
    val publicationName = name
    // kotlin independent uppercase
    val upperCaseName = Character.toUpperCase(publicationName.first()) + publicationName.drop(1)
    val patchTaskName = "patchModuleJsonFor$upperCaseName"
    val generateMetadataTaskName = "generateMetadataFileFor${upperCaseName}Publication"
    val projectName = project.name
    val projectVersion = project.version.toString()
    val moduleJsonFile = project.layout.buildDirectory.file("publications/$publicationName/module.json").get().asFile

    val patch = project.tasks.register(patchTaskName) {
        group = "patch"

        doLast {
            if (!moduleJsonFile.exists()) {
                return@doLast
            }

            moduleJsonFile.fixMetadata(projectName, projectVersion)

            logger.info("Updated metadata for $publicationName publication")
        }
    }

    project.tasks.named(generateMetadataTaskName) {
        finalizedBy(patch)
    }
}

private fun File.fixMetadata(projectName: String, projectVersion: String) {
    val text = readText()

    val newText = text
        .updateFilesNameField(projectName)
        .updateAvailableAtModuleField(projectName)
        .updateAvailableAtUrlField(projectName, projectVersion)

    writeText(newText)
}

// in "files" object:
// from: "name": "krpc-client-metadata-0.2.0.jar",
// to: "name": "kotlinx-rpc-krpc-client-metadata-0.2.0.jar",
private fun String.updateFilesNameField(projectName: String): String {
    return replace(
        keyValue("name", projectName, openValue = true),
        keyValue("name", "$KOTLINX_RPC_PREFIX-$projectName", openValue = true),
    )
}

// in "available-at" object:
// from: "module": "krpc-client-iossimulatorarm64",
// to: "module": "kotlinx-rpc-krpc-client-iossimulatorarm64",
private fun String.updateAvailableAtModuleField(projectName: String): String {
    return replace(
        keyValue("module", projectName, openValue = true),
        keyValue("module", "$KOTLINX_RPC_PREFIX-$projectName", openValue = true),
    )
}

// in "available-at" object:
// from: "url": "../../krpc-client-iossimulatorarm64/0.2.0/krpc-client-iossimulatorarm64-0.2.0.module",
// to: "url": "../../kotlinx-rpc-krpc-client-iossimulatorarm64/0.2.0/kotlinx-rpc-krpc-client-iossimulatorarm64-0.2.0.module",
private fun String.updateAvailableAtUrlField(projectName: String, version: String): String {
    return replace(
        availableAtUrl(projectName, version, "(\\w+)", "(.+)").toRegex(),
        availableAtUrl("$KOTLINX_RPC_PREFIX-$projectName", version, "$1", "$3"),
    )
}

private fun availableAtUrl(projectName: String, version: String, variant: String, suffix: String): String {
    val safeVersion = version.escapeDots()
    return keyValue("url", "../../$projectName-$variant/$safeVersion/$projectName-$variant-$safeVersion$suffix")
}

private fun keyValue(key: String, value: String, openValue: Boolean = false): String {
    return "\"$key\": \"$value" + ('"'.takeIf { !openValue } ?: "")
}

private fun String.escapeDots() = replace(".", "\\.")

infix fun <T: Any> Property<T>.by(value: T) {
    set(value)
}

class PublicationRepositoryConfig {
    var url: String? = null
    var name: String? = null
    var username: String? = null
    var password: String? = null
}

fun RepositoryHandler.configureRepository(
    project: Project,
    configBuilder: PublicationRepositoryConfig.() -> Unit,
) {
    val config = PublicationRepositoryConfig().apply(configBuilder)
    val url = config.url ?: run {
        project.logger.info("No ${config.name} URL provided, skipping repository configuration")
        return
    }

    val usernameProperty = config.username ?: configError("username")
    val passwordProperty = config.password ?: configError("password")

    val usernameValue = project.getSensitiveProperty(usernameProperty)
    val passwordValue = project.getSensitiveProperty(passwordProperty)

    if (usernameValue == null || passwordValue == null) {
        val usernameProvided = usernameValue != null
        val passwordProvided = passwordValue != null
        project.logger.info(
            "No ${config.name} credentials provided " +
                    "(username: $usernameProvided, password: $passwordProvided), " +
                    "skipping repository configuration"
        )
        return
    }

    maven(url = url) {
        name = config.name ?: configError("name")

        credentials {
            username = usernameValue
            password = passwordValue
        }
    }

    project.logger.info("Configured ${config.name} repository for publication")
}

private fun configError(parameterName: String): Nothing {
    error("Expected not null $parameterName for publication repository config")
}
