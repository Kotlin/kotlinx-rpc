/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("DuplicatedCode")

pluginManagement {
    fun logAbsentProperty(name: String): Nothing? {
        logger.info("Property '$name' is not present for repository credentials.")

        return null
    }

    fun getSpaceUsername(): String? {
        val username = "kotlinx.rpc.team.space.username"
        return settings.providers.gradleProperty(username).orNull
            ?: System.getenv(username)?.ifEmpty { null }
            ?: logAbsentProperty(username)
    }

    fun getSpacePassword(): String? {
        val password = "kotlinx.rpc.team.space.password"
        return settings.providers.gradleProperty(password).orNull
            ?: System.getenv(password)?.ifEmpty { null }
            ?: logAbsentProperty(password)
    }

    /**
     * Creates a publishing repository targeting Space Packages on jetbrains.team.
     *
     * @param repoName the name of the Space Packages repository
     */
    fun RepositoryHandler.jbTeamPackages(repoName: String) {
        maven {
            name = repoName.split("-").joinToString("") { it.replaceFirstChar { c -> c.titlecase() } }
            url = uri("https://packages.jetbrains.team/maven/p/krpc/$repoName")

            val spaceUsername = getSpaceUsername()
            val spacePassword = getSpacePassword()

            if (spaceUsername != null && spacePassword != null) {
                credentials {
                    username = spaceUsername
                    password = spacePassword
                }
            }
        }
    }

    fun RepositoryHandler.buildDeps() = jbTeamPackages(repoName = "build-deps")
    fun RepositoryHandler.buildDepsEap() = jbTeamPackages(repoName = "build-deps-eap")

    repositories {
        buildDeps()
        buildDepsEap()
    }
}

gradle.rootProject {
    fun logAbsentProperty(name: String): Nothing? {
        logger.info("Property '$name' is not present for repository credentials.")

        return null
    }

    fun getSpaceUsername(): String? {
        val username = "kotlinx.rpc.team.space.username"
        return settings.providers.gradleProperty(username).orNull
            ?: System.getenv(username)?.ifEmpty { null }
            ?: logAbsentProperty(username)
    }

    fun getSpacePassword(): String? {
        val password = "kotlinx.rpc.team.space.password"
        return settings.providers.gradleProperty(password).orNull
            ?: System.getenv(password)?.ifEmpty { null }
            ?: logAbsentProperty(password)
    }

    /**
     * Creates a publishing repository targeting Space Packages on jetbrains.team.
     *
     * @param repoName the name of the Space Packages repository
     */
    fun RepositoryHandler.jbTeamPackages(repoName: String) {
        maven {
            name = repoName.split("-").joinToString("") { it.replaceFirstChar { c -> c.titlecase() } }

            url = uri("https://packages.jetbrains.team/maven/p/krpc/$repoName")

            val spaceUsername = getSpaceUsername()
            val spacePassword = getSpacePassword()

            if (spaceUsername != null && spacePassword != null) {
                credentials {
                    username = spaceUsername
                    password = spacePassword
                }
            }
        }
    }

    fun RepositoryHandler.buildDeps() = jbTeamPackages(repoName = "build-deps")
    fun RepositoryHandler.buildDepsEap() = jbTeamPackages(repoName = "build-deps-eap")

    allprojects {
        buildscript {
            repositories {
                buildDeps()
                buildDepsEap()
            }
        }
        repositories {
            buildDeps()
            buildDepsEap()
        }
    }
}
