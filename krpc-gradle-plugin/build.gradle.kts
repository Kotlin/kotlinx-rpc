plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    // brings `java-platform` that messes up kotlin compiler version
    // but needed to have `publish to gradle plugin portal` task, todo
//    alias(libs.plugins.gradle.plugin.publish) apply false
    alias(libs.plugins.gradle.kotlin.dsl) apply false
    alias(libs.plugins.conventions.common) apply false
    `maven-publish`
}

subprojects {
    group = "org.jetbrains.krpc"
    version = rootProject.libs.versions.krpc.core.get()

    fun alias(notation: Provider<PluginDependency>): String {
        return notation.get().pluginId
    }

    plugins.apply(alias(rootProject.libs.plugins.kotlin.jvm))
//    plugins.apply(alias(rootProject.libs.plugins.gradle.plugin.publish))
    plugins.apply(alias(rootProject.libs.plugins.gradle.kotlin.dsl))
    plugins.apply(alias(rootProject.libs.plugins.conventions.common))
    plugins.apply("org.gradle.maven-publish")

    project.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(8))
        }
    }

    // This block is needed to show plugin tasks on --dry-run
    //  and to not run task actions on ":plugin:task --dry-run".
    //  The bug is known since June 2017 and still not fixed.
    //  The workaround used below is described here: https://github.com/gradle/gradle/issues/2517#issuecomment-437490287
    if (gradle.parent != null && gradle.parent!!.startParameter.isDryRun) {
        gradle.startParameter.isDryRun = true
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
//    "publishPlugins", // publish to Gradle Plugin Portal
)
