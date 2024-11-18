/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("DuplicatedCode")

pluginManagement {
    fun findGlobalRootDirPath(): java.nio.file.Path {
        var path = file(".").toPath().toAbsolutePath()

        // we assume that the 'versions-root' folder can only be present in the root folder
        while (
            java.nio.file.Files.newDirectoryStream(path).use { it.toList() }.none {
                java.nio.file.Files.isDirectory(it) && it.fileName.toString() == "versions-root"
            }
        ) {
            path = path.parent ?: error("Unable to find root path for kotlinx.rpc project")
        }

        return path
    }

    fun logAbsentProperty(name: String): Nothing? {
        logger.info("Property '$name' is not present for repository credentials.")

        return null
    }

    fun getEnv(propertyName: String): String? = System.getenv(
        propertyName.replace(".", "_").uppercase()
    )?.ifEmpty { null }

    fun getLocalProperties(): java.util.Properties {
        return java.util.Properties().apply {
            val propertiesDir = File(
                rootDir.path
                    .removeSuffix("/gradle-conventions")
                    .removeSuffix("/gradle-conventions-settings")
                    .removeSuffix("/compiler-plugin")
                    .removeSuffix("/gradle-plugin")
            )
            val localFile = File(propertiesDir, "local.properties")
            if (localFile.exists()) {
                localFile.inputStream().use { load(it) }
            }
        }
    }

    fun getSpacePassword(): String? {
        val password = "kotlinx.rpc.team.space.password"
        return getLocalProperties()[password] as String?
            ?: settings.providers.gradleProperty(password).orNull
            ?: getEnv(password)
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

            val spacePassword = getSpacePassword()

            if (spacePassword != null) {
                credentials(HttpHeaderCredentials::class.java) {
                    name = "Authorization"
                    value = "Bearer $spacePassword"
                }

                authentication {
                    create<HttpHeaderAuthentication>("http_auth_header")
                }
            } else {
                logger.info("Skipping adding credentials for Space repository '$repoName'")
            }
        }
    }

    fun RepositoryHandler.buildDeps() = jbTeamPackages(repoName = "build-deps")
    fun RepositoryHandler.buildDepsEap() = jbTeamPackages(repoName = "build-deps-eap")

    repositories {
        val useProxyProperty = getLocalProperties()["kotlinx.rpc.useProxyRepositories"] as String?
        val useProxy = useProxyProperty == null || useProxyProperty == "true"

        if (useProxy) {
            buildDeps()
            buildDepsEap()
        } else {
            mavenCentral()
            gradlePluginPortal()
        }

        maven("${findGlobalRootDirPath()}/lib-kotlin/")
    }
}

gradle.rootProject {
    fun findGlobalRootDirPath(): java.nio.file.Path {
        var path = file(".").toPath().toAbsolutePath()

        // we assume that the 'versions-root' folder can only be present in the root folder
        while (
            java.nio.file.Files.newDirectoryStream(path).use { it.toList() }.none {
                java.nio.file.Files.isDirectory(it) && it.fileName.toString() == "versions-root"
            }
        ) {
            path = path.parent ?: error("Unable to find root path for kotlinx.rpc project")
        }

        return path
    }

    fun logAbsentProperty(name: String): Nothing? {
        logger.info("Property '$name' is not present for repository credentials.")

        return null
    }

    fun getEnv(propertyName: String): String? = System.getenv(
        propertyName.replace(".", "_").uppercase()
    )?.ifEmpty { null }

    fun getLocalProperties(): java.util.Properties {
        return java.util.Properties().apply {
            val propertiesDir = File(
                rootDir.path
                    .removeSuffix("/gradle-conventions")
                    .removeSuffix("/gradle-conventions-settings")
                    .removeSuffix("/compiler-plugin")
                    .removeSuffix("/gradle-plugin")
            )
            val localFile = File(propertiesDir, "local.properties")
            if (localFile.exists()) {
                localFile.inputStream().use { load(it) }
            }
        }
    }

    fun java.util.Properties.isUsingProxyRepositories(): Boolean {
        val useProxyProperty = this["kotlinx.rpc.useProxyRepositories"] as String?
        return useProxyProperty == null || useProxyProperty == "true"
    }

    fun getSpacePassword(): String? {
        val password = "kotlinx.rpc.team.space.password"
        return getLocalProperties()[password] as String?
            ?: settings.providers.gradleProperty(password).orNull
            ?: getEnv(password)
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

            val spacePassword = getSpacePassword()

            if (spacePassword != null) {
                credentials(HttpHeaderCredentials::class.java) {
                    name = "Authorization"
                    value = "Bearer $spacePassword"
                }

                authentication {
                    create<HttpHeaderAuthentication>("http_auth_header")
                }
            } else {
                logger.info("Skipping adding credentials for Space repository '$repoName'")
            }
        }
    }

    fun RepositoryHandler.buildDeps() = jbTeamPackages(repoName = "build-deps")
    fun RepositoryHandler.buildDepsEap() = jbTeamPackages(repoName = "build-deps-eap")

    allprojects {
        val localProps = getLocalProperties()

        this.extra["spacePassword"] = getSpacePassword()
        this.extra["localProperties"] = localProps
        this.extra["useProxyRepositories"] = localProps.isUsingProxyRepositories()

        val useProxy = localProps.isUsingProxyRepositories()

        val globalRootDir = findGlobalRootDirPath()

        buildscript {
            repositories {
                if (useProxy) {
                    buildDeps()
                    buildDepsEap()
                } else {
                    mavenCentral()
                    gradlePluginPortal()
                }

                maven("$globalRootDir/lib-kotlin/")
            }
        }
        repositories {
            if (useProxy) {
                buildDeps()
                buildDepsEap()
            } else {
                mavenCentral()
                gradlePluginPortal()

                maven("https://www.jetbrains.com/intellij-repository/releases")

                maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-ide-plugin-dependencies")
                maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
                maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")

                maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
            }

            maven("$globalRootDir/lib-kotlin/")
        }
    }
}
