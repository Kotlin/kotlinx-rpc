import org.jetbrains.kotlin.gradle.plugin.*

//  ### Utils ###

fun capitalize(string: String): String {
    if (string.isEmpty()) {
        return ""
    }
    val firstChar = string[0]
    return string.replaceFirst(firstChar, Character.toTitleCase(firstChar))
}

object CSM {
    const val KOTLIN_MULTIPLATFORM_PLUGIN_ID = "org.jetbrains.kotlin.multiplatform"
    const val KOTLIN_JVM_PLUGIN_ID = "org.jetbrains.kotlin.jvm"

    const val KRPC_SERVICE_LOADER_MODULE = ":krpc-utils:krpc-utils-service-loader"
}

val kotlinVersion = getKotlinPluginVersion()

// ### Plugin Logic ###
// What happens here, is that root module is being made a complete compile specific module for current version of Kotlin
// We take -core submodule that contains compiler version independent code, add to it version specific module
// and the summary is presented to the outer world as a complete module, which is the root module for these two submodules
// Root module takes its submodules as `api` gradle configurations, and root's jar will consist of
// two submodules' artifacts and will be presented to the world as it was always a one complete module.

val rootProjectPrefix = "$name-"
val coreProjectName = "${rootProjectPrefix}core"

// configurations are available only when kotlin plugin is applied, so we use lazy resolving
fun Project.lazyDependency(baseConfigurationName: String, notation: Any) {
    val kmpConfigurationName = "commonMain${capitalize(baseConfigurationName)}"
    this.configurations.matching { it.name == kmpConfigurationName }.all {
        this@lazyDependency.dependencies.add(kmpConfigurationName, notation)
    }

    this.configurations.matching { it.name == baseConfigurationName }.all {
        this@lazyDependency.dependencies.add(baseConfigurationName, notation)
    }
}

fun Project.lazyApi(notation: Any) {
    lazyDependency("api", notation)
}

val root = project

subprojects {
    when {
        name == coreProjectName -> {
            lazyApi(project(CSM.KRPC_SERVICE_LOADER_MODULE))

            // This and the same for version specific submodule causes
            //
            // Could not determine the dependencies of task ':krpc-compiler-plugin:compileJava'.
            // > Cannot change dependencies of dependency configuration ':krpc-compiler-plugin:api' after it has been included in dependency resolution.
            //
            // on linuxX64Test for some reason. For js and jvm works ok
            // disabling this target for 1.7.* for now
            root.lazyApi(this)
        }

        name.startsWith(rootProjectPrefix) -> {
            val semVer = name
                .removePrefix(rootProjectPrefix)
                .replace('_', '.')

            // resolve compiler specific submodule, for example krpc-compiler-plugin-1_7 for Kotlin version 1.7.22
            if (kotlinVersion.startsWith(semVer)) {
                val coreProject = root.subprojects.singleOrNull { it.name == coreProjectName }
                    ?: error("Expected to find subproject with name '$coreProjectName'")

                lazyApi(project(CSM.KRPC_SERVICE_LOADER_MODULE))
                lazyApi(coreProject)

                root.lazyApi(this)
            }
        }
    }
}
