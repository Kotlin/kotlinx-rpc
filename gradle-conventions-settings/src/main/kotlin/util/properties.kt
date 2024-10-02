/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("DuplicatedCode")

package util

import org.gradle.api.Project

fun Project.logAbsentProperty(name: String): Nothing? {
    logger.info("Property '$name' is not present.")

    return null
}

fun getEnv(propertyName: String): String? = System.getenv(
    propertyName.replace(".", "_").uppercase()
)?.ifEmpty { null }

private const val SPACE_USERNAME = "kotlinx.rpc.team.space.username"

fun Project.getSpaceUsername(): String? {
    return providers.gradleProperty(SPACE_USERNAME).orNull
        ?: getEnv(SPACE_USERNAME)
        ?: logAbsentProperty(SPACE_USERNAME)
}

private const val SPACE_PASSWORD = "kotlinx.rpc.team.space.password"

fun Project.getSpacePassword(): String? {
    return providers.gradleProperty(SPACE_PASSWORD).orNull
        ?: getEnv(SPACE_PASSWORD)
        ?: logAbsentProperty(SPACE_USERNAME)
}
