plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.gradle.plugin.publish) apply false
    alias(libs.plugins.conventions.common) apply false
}

subprojects {
    group = "org.jetbrains.krpc"
    version = rootProject.libs.versions.krpc.core.get()

    fun alias(notation: Provider<PluginDependency>): String {
        return notation.get().pluginId
    }

    plugins.apply(alias(rootProject.libs.plugins.kotlin.jvm))
    plugins.apply(alias(rootProject.libs.plugins.gradle.plugin.publish))
    plugins.apply(alias(rootProject.libs.plugins.conventions.common))

    project.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(8))
        }
    }
}

fun Project.configureMetaTasks(vararg taskNames: String) {
    val root = this
    val metaSet = taskNames.toSet()

    subprojects.map {
        it.tasks.all {
            val subtask = this

            if (subtask.name in metaSet) {
                val metaTask = root.tasks.findByName(subtask.name)
                    ?: root.task(subtask.name)

                metaTask.dependsOn(subtask)

                metaTask.group = "plugins meta"
            }
        }
    }
}

configureMetaTasks(
    "publish", // publish to Space
    "publishToMavenLocal", // for local plugin development
    "validatePlugins", // plugin validation
    "publishPlugins", // publish to Gradle Plugin Portal
)
