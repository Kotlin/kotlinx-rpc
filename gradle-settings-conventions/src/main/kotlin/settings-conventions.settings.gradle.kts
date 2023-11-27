import java.nio.file.Path
import kotlin.io.path.bufferedReader
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries

object SettingsConventions {
    const val KOTLIN_VERSION_ENV_VAR_NAME = "KOTLIN_VERSION"
    const val EAP_VERSION_ENV_VAR_NAME = "EAP_VERSION"

    const val KSP_VERSION_ALIAS = "ksp"
    const val KRPC_CORE_VERSION_ALIAS = "krpc-core"
    const val KRPC_FULL_VERSION_ALIAS = "krpc-full"
    const val KOTLIN_VERSION_ALIAS = "kotlin-lang"
    const val VERSIONS_SECTION_NAME = "[versions]"

    const val GRADLE_WRAPPER_FOLDER = "gradle"
    const val LIBS_VERSION_CATALOG_PATH = "$GRADLE_WRAPPER_FOLDER/libs.versions.toml"
    const val KSP_VERSION_PATH = "$GRADLE_WRAPPER_FOLDER/ksp.versions.json"
}

gradle.rootProject {
    allprojects {
        buildscript {
            repositories {
                gradlePluginPortal()
                mavenCentral()
            }
        }

        repositories {
            mavenCentral()
        }
    }
}

fun findGlobalRootDirPath(start: Path, onDir: () -> Unit = {}): Path {
    var path = start

    // we assume that `gradle` folder can only be present in the root folder
    while (
        path.listDirectoryEntries().none {
            it.isDirectory() && it.fileName.toString() == SettingsConventions.GRADLE_WRAPPER_FOLDER
        }
    ) {
        path = path.parent ?: error("Unable to find root path for kRPC project")
        onDir()
    }

    return path
}

fun getKspVersionByKotlinVersion(rootDir: Path, kotlinVersion: String): String {
    val file = File("${findGlobalRootDirPath(rootDir)}/${SettingsConventions.KSP_VERSION_PATH}")

    @Suppress("UNCHECKED_CAST")
    val kspVersionLookup = groovy.json.JsonSlurper()
        .parseText(file.readText()) as Map<String, String>

    val kspVersion = kspVersionLookup[kotlinVersion]
        ?: error("KSP version for Kotlin '$kotlinVersion' is not found")

    return "$kotlinVersion-$kspVersion"
}

// I would love to use tomlj parser here, but I could import it :(
fun resolveCatalogVersions(rootDir: Path): Map<String, String> {
    var versionsStarted = false
    val map = mutableMapOf<String, String>()

    rootDir.resolve(SettingsConventions.LIBS_VERSION_CATALOG_PATH).bufferedReader().use { reader ->
        while (true) {
            val line = reader.readLine() ?: break

            if (versionsStarted) {
                // versions section ended
                if (line.startsWith("[")) {
                    break
                }

                // remove comments
                val versionEntry = line.substringBefore("#").trim()
                if (versionEntry.isNotEmpty()) {
                    val (key, value) = versionEntry.split("=").map {
                        it.trim { ch -> ch == '"' || ch.isWhitespace() }
                    }
                    map[key] = value
                }
            } else {
                if (line.startsWith(SettingsConventions.VERSIONS_SECTION_NAME)) {
                    versionsStarted = true
                }
            }
        }
    }

    return map
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            var isRoot = true
            val currentPath = file(".").toPath().toAbsolutePath()

            val rootDir = findGlobalRootDirPath(currentPath) { isRoot = false }

            // avoid "Problem: In version catalog libs, you can only call the 'from' method a single time."
            if (!isRoot) {
                from(files("$rootDir/${SettingsConventions.LIBS_VERSION_CATALOG_PATH}"))
            }

            val catalogVersions = resolveCatalogVersions(rootDir)

            var kotlinCatalogVersion: String? = System.getenv(SettingsConventions.KOTLIN_VERSION_ENV_VAR_NAME)
            if (kotlinCatalogVersion != null) {
                version(SettingsConventions.KOTLIN_VERSION_ALIAS, kotlinCatalogVersion)
            } else {
                kotlinCatalogVersion = catalogVersions[SettingsConventions.KOTLIN_VERSION_ALIAS]
            }
            val kotlinVersion = kotlinCatalogVersion
                ?: error("Expected to resolve '${SettingsConventions.KOTLIN_VERSION_ALIAS}' version")

            version(SettingsConventions.KSP_VERSION_ALIAS) {
                val kspVersion = getKspVersionByKotlinVersion(rootDir, kotlinVersion)

                require(kspVersion)
            }

            val eapVersion: String = System.getenv(SettingsConventions.EAP_VERSION_ENV_VAR_NAME)
                ?.let { "-eap-$it" } ?: ""
            val krpcCatalogVersion = catalogVersions[SettingsConventions.KRPC_CORE_VERSION_ALIAS]
                ?: error("Expected to resolve '${SettingsConventions.KRPC_CORE_VERSION_ALIAS}' version")
            val krpcVersion = krpcCatalogVersion + eapVersion
            val krpcFullVersion = "$kotlinVersion-$krpcVersion"

            version(SettingsConventions.KRPC_CORE_VERSION_ALIAS, krpcVersion)
            version(SettingsConventions.KRPC_FULL_VERSION_ALIAS, krpcFullVersion)
        }
    }
}
