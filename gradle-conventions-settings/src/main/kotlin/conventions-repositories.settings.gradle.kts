/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
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

    fun getEnv(propertyName: String): String? =
        settings.providers.environmentVariable(
            propertyName.replace(".", "_").uppercase()
        ).orNull?.ifEmpty { null }

    val localProperties: java.util.Properties by lazy {
        java.util.Properties().apply {
            val propertiesDir = File(
                rootDir.path
                    .removeSuffix("/gradle-conventions")
                    .removeSuffix("/gradle-conventions-settings")
                    .removeSuffix("/compiler-plugin")
                    .removeSuffix("/gradle-plugin")
                    .removeSuffix("/dokka-plugin")
            )
            val localFile = File(propertiesDir, "local.properties")
            if (localFile.exists()) {
                localFile.inputStream().use { load(it) }
            }
        }
    }

    val spacePassword: String? by lazy {
        val password = "kotlinx.rpc.team.space.password"
        localProperties[password] as String?
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

            val pw = spacePassword

            if (pw != null) {
                credentials(HttpHeaderCredentials::class.java) {
                    name = "Authorization"
                    value = "Bearer $pw"
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

    val globalRootDir = findGlobalRootDirPath()

    repositories {
        val useProxyProperty = localProperties["kotlinx.rpc.useProxyRepositories"] as String?
        val useProxy = (useProxyProperty == null || useProxyProperty == "true") &&
                settings.providers.gradleProperty("kotlinx.rpc.useProxyRepositories").orNull != "false"

        if (useProxy) {
            buildDeps()
            buildDepsEap()
        } else {
            mavenCentral()
            gradlePluginPortal()
            google()
        }

        maven("$globalRootDir/lib-kotlin/")
    }
}

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

fun getEnv(propertyName: String): String? =
    settings.providers.environmentVariable(
        propertyName.replace(".", "_").uppercase()
    ).orNull?.ifEmpty { null }

val localProperties: java.util.Properties by lazy {
    java.util.Properties().apply {
        val propertiesDir = File(
            rootDir.path
                .removeSuffix("/gradle-conventions")
                .removeSuffix("/gradle-conventions-settings")
                .removeSuffix("/compiler-plugin")
                .removeSuffix("/gradle-plugin")
                .removeSuffix("/dokka-plugin")
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

val spacePassword: String? by lazy {
    val password = "kotlinx.rpc.team.space.password"
    localProperties[password] as String?
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

        val pw = spacePassword

        if (pw != null) {
            credentials(HttpHeaderCredentials::class.java) {
                name = "Authorization"
                value = "Bearer $pw"
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
fun RepositoryHandler.grpc() = jbTeamPackages(repoName = "grpc")

val globalRootDir = findGlobalRootDirPath()

settings.extra["spacePassword"] = spacePassword
settings.extra["localProperties"] = localProperties
settings.extra["useProxyRepositories"] = localProperties.isUsingProxyRepositories()

gradle.rootProject {
    allprojects {
        val useProxy = localProperties.isUsingProxyRepositories() &&
                project.findProperty("kotlinx.rpc.useProxyRepositories") != "false"

        this.extra["spacePassword"] = spacePassword
        this.extra["localProperties"] = localProperties
        this.extra["useProxyRepositories"] = useProxy

        buildscript {
            repositories {
                if (useProxy) {
                    buildDeps()
                    buildDepsEap()
                } else {
                    mavenCentral()
                    gradlePluginPortal()
                    google()
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
                google()

                maven("https://www.jetbrains.com/intellij-repository/releases")

                maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-ide-plugin-dependencies")
                maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
                maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")

                maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
                maven("https://packages.jetbrains.team/maven/p/ktor/eap")
            }

            // Internal standalone shim artifacts are published here and can be resolved
            // from any consumer project in this composite build.
            // Remove it once we release them to maven central. (KRPC-539)
            grpc()
            // Local development repository for native-deps shim artifacts.
            // Shims are published here via publishAllPublicationsToNativeDepsBuildRepoRepository.
            maven("$globalRootDir/native-deps/build/repo")
            maven("$globalRootDir/lib-kotlin/")
        }
    }
}
