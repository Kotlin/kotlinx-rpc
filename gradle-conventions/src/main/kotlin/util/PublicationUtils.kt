/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.maven

infix fun <T> Property<T>.by(value: T) {
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

fun Project.getSensitiveProperty(name: String?): String? {
    if (name == null) {
        error("Expected not null property 'name' for publication repository config")
    }

    return project.findProperty(name) as? String
        ?: System.getenv(name)
        ?: System.getProperty(name)
}

fun configError(parameterName: String): Nothing {
    error("Expected not null $parameterName for publication repository config")
}
