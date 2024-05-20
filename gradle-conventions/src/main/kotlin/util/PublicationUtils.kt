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
    var user: String? = null
    var password: String? = null
}

fun RepositoryHandler.configureRepository(
    project: Project,
    configBuilder: PublicationRepositoryConfig.() -> Unit,
) {
    val config = PublicationRepositoryConfig().apply(configBuilder)

    val userProperty = config.user ?: configError("userPropertyName")
    val passwordProperty = config.password ?: configError("passwordPropertyName")

    val userValue = project.getSensitiveProperty(userProperty)
    val passwordValue = project.getSensitiveProperty(passwordProperty)

    if (userValue == null || passwordValue == null) {
        return
    }

    val url = config.url ?: configError("url")

    maven(url = url) {
        name = config.name ?: configError("name")

        credentials {
            username = userValue
            password = passwordValue
        }
    }
}

fun Project.getSensitiveProperty(name: String?): String? {
    if (name == null) {
        error("Expected not null property name for publication repository config")
    }

    return project.findProperty(name) as? String ?: System.getenv(name)
}

fun configError(parameterName: String): Nothing {
    error("Expected not null $parameterName for publication repository config")
}
