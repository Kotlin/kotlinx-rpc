import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path

fun Path.bufferedReader(
    charset: Charset = Charsets.UTF_8,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    vararg options: OpenOption
): BufferedReader {
    return BufferedReader(
        InputStreamReader(
            Files.newInputStream(this, *options),
            charset
        ),
        bufferSize
    )
}

// actual libraries versions for current Kotlin version.
// Resolved from 'gradle/targets-since-kotlin-lookup.json'
data class KotlinLookupTable(
    val ksp: String,
    val atomicfu: String,
    val serialization: String,
    val coroutines: String,
    val ktor: String,
    val kotlinLogging: String,
    val gradleKotlinDsl: String,
    val gradlePluginPublish: String,
)

object SettingsConventions {
    const val KOTLIN_VERSION_ENV_VAR_NAME = "KOTLIN_VERSION"
    const val EAP_VERSION_ENV_VAR_NAME = "EAP_VERSION"

    const val KSP_VERSION_ALIAS = "ksp"
    const val ATOMICFU_VERSION_ALIAS = "atomicfu"
    const val SERIALIZATION_VERSION_ALIAS = "serialization"
    const val COROUTINES_VERSION_ALIAS = "coroutines"
    const val KTOR_VERSION_ALIAS = "ktor"
    const val KOTLIN_LOGGING_VERSION_ALIAS = "kotlin-logging"
    const val KOTLIN_DSL_VERSION_ALIAS = "gradle-kotlin-dsl"
    const val GRADLE_PUBLISH_VERSION_ALIAS = "gradle-plugin-publish"
    const val KRPC_CORE_VERSION_ALIAS = "krpc-core"
    const val KRPC_FULL_VERSION_ALIAS = "krpc-full"
    const val KOTLIN_VERSION_ALIAS = "kotlin-lang"

    const val VERSIONS_SECTION_NAME = "[versions]"

    const val GRADLE_WRAPPER_FOLDER = "gradle"
    const val LIBS_VERSION_CATALOG_PATH = "$GRADLE_WRAPPER_FOLDER/libs.versions.toml"
    const val KOTLIN_VERSIONS_LOOKUP_PATH = "$GRADLE_WRAPPER_FOLDER/kotlin-versions-lookup.json"
}

// ### VERSION RESOLVING SECTION ###
// This sections of the plugin is responsible for setting properer libraries versions in the project
// according to the current Kotlin version that should be used.

// This plugin can be applied in different subprojects, so we need a way to find global project root for kRPC project
// to be able to resolve 'gradle/libs.versions.toml' and other files
fun findGlobalRootDirPath(start: Path, onDir: () -> Unit = {}): Path {
    var path = start

    // we assume that `gradle` folder can only be present in the root folder
    while (
        Files.newDirectoryStream(path).use { it.toList() }.none {
            Files.isDirectory(it) && it.fileName.toString() == SettingsConventions.GRADLE_WRAPPER_FOLDER
        }
    ) {
        path = path.parent ?: error("Unable to find root path for kRPC project")
        onDir()
    }

    return path
}

// Resolves 'gradle/targets-since-kotlin-lookup.json'
fun loadLookupTable(rootDir: Path, kotlinVersion: String): KotlinLookupTable {
    val file = rootDir.resolve(SettingsConventions.KOTLIN_VERSIONS_LOOKUP_PATH).toFile()

    @Suppress("UNCHECKED_CAST")
    val fullTable = groovy.json.JsonSlurper()
        .parseText(file.readText()) as Map<String, Map<String, String>>

    val currentTable = fullTable[kotlinVersion]
        ?: error("Unsupported Kotlin version in lookup: $kotlinVersion. Available: ${fullTable.keys.joinToString()}")

    return KotlinLookupTable(
        ksp = currentTable.getValue(SettingsConventions.KSP_VERSION_ALIAS),
        atomicfu = currentTable.getValue(SettingsConventions.ATOMICFU_VERSION_ALIAS),
        serialization = currentTable.getValue(SettingsConventions.SERIALIZATION_VERSION_ALIAS),
        coroutines = currentTable.getValue(SettingsConventions.COROUTINES_VERSION_ALIAS),
        ktor = currentTable.getValue(SettingsConventions.KTOR_VERSION_ALIAS),
        kotlinLogging = currentTable.getValue(SettingsConventions.KOTLIN_LOGGING_VERSION_ALIAS),
        gradleKotlinDsl = currentTable.getValue(SettingsConventions.KOTLIN_DSL_VERSION_ALIAS),
        gradlePluginPublish = currentTable.getValue(SettingsConventions.GRADLE_PUBLISH_VERSION_ALIAS),
    )
}

// Resolves [versions] section from 'gradle/libs.versions.toml' into map
//
// NOTE: I would love to use tomlj parser here, but I could import it :(
fun resolveVersionCatalog(rootDir: Path): Map<String, String> {
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

// Uses KOTLIN_VERSION env var if present for project's Kotlin version and sets it into Version Catalog.
// Otherwise uses version from catalog.
fun VersionCatalogBuilder.resolveKotlinVersion(versionCatalog: Map<String, String>): String {
    var kotlinCatalogVersion: String? = System.getenv(SettingsConventions.KOTLIN_VERSION_ENV_VAR_NAME)

    if (kotlinCatalogVersion != null) {
        version(SettingsConventions.KOTLIN_VERSION_ALIAS, kotlinCatalogVersion)
    } else {
        kotlinCatalogVersion = versionCatalog[SettingsConventions.KOTLIN_VERSION_ALIAS]
    }

    return kotlinCatalogVersion
        ?: error("Expected to resolve '${SettingsConventions.KOTLIN_VERSION_ALIAS}' version")
}

// Resolves core krpc version (without Kotlin version prefix) from Versions Catalog.
// Updates it with EAP_VERSION suffix of present.
// Sets krpc full version (with Kotlin version prefix) into Version Catalog.
fun VersionCatalogBuilder.resolveKrpcVersion(versionCatalog: Map<String, String>, kotlinVersion: String) {
    val eapVersion: String = System.getenv(SettingsConventions.EAP_VERSION_ENV_VAR_NAME)
        ?.let { "-eap-$it" } ?: ""
    val krpcCatalogVersion = versionCatalog[SettingsConventions.KRPC_CORE_VERSION_ALIAS]
        ?: error("Expected to resolve '${SettingsConventions.KRPC_CORE_VERSION_ALIAS}' version")
    val krpcVersion = krpcCatalogVersion + eapVersion
    val krpcFullVersion = "$kotlinVersion-$krpcVersion"

    version(SettingsConventions.KRPC_CORE_VERSION_ALIAS, krpcVersion)
    version(SettingsConventions.KRPC_FULL_VERSION_ALIAS, krpcFullVersion)
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

            val versionCatalog = resolveVersionCatalog(rootDir)

            val kotlinVersion = resolveKotlinVersion(versionCatalog)

            extra["kotlinVersion"] = kotlinVersion

            resolveKrpcVersion(versionCatalog, kotlinVersion)

            // Other Kotlin-dependant versions 
            val lookupTable = loadLookupTable(rootDir, kotlinVersion)

            version(SettingsConventions.KSP_VERSION_ALIAS) {
                strictly("$kotlinVersion-${lookupTable.ksp}")
            }

            version(SettingsConventions.ATOMICFU_VERSION_ALIAS) {
                strictly(lookupTable.atomicfu)
            }

            version(SettingsConventions.SERIALIZATION_VERSION_ALIAS) {
                strictly(lookupTable.serialization)
            }

            version(SettingsConventions.COROUTINES_VERSION_ALIAS) {
                strictly(lookupTable.coroutines)
            }

            version(SettingsConventions.KTOR_VERSION_ALIAS) {
                strictly(lookupTable.ktor)
            }

            version(SettingsConventions.KOTLIN_LOGGING_VERSION_ALIAS) {
                strictly(lookupTable.kotlinLogging)
            }

            version(SettingsConventions.KOTLIN_DSL_VERSION_ALIAS) {
                strictly(lookupTable.gradleKotlinDsl)
            }

            version(SettingsConventions.GRADLE_PUBLISH_VERSION_ALIAS) {
                strictly(lookupTable.gradlePluginPublish)
            }
        }
    }
}

// ### OTHER SETTINGS SECTION ###

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
