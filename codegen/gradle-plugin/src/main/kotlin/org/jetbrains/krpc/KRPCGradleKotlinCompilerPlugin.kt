package org.jetbrains.krpc

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import com.google.devtools.ksp.gradle.KspExtension
import com.google.devtools.ksp.gradle.KspTask

class KRPCGradleKotlinCompilerPlugin : KotlinCompilerPluginSupportPlugin {
    private val moduleIds = mutableMapOf<String, String>()

    override fun apply(target: Project) {
        target.plugins.apply(this::class.java)

        val extension = target.extensions.findByType(KspExtension::class.java)
            ?: error("Expected KspExtension to be present")

        target.tasks.withType(KspTask::class.java) { task ->
            task.doFirst {
                extension.apply {
                    arg("moduleId", moduleIds[task.name] ?: error("Expected moduleId for ${task.name}"))
                }
            }
        }
    }

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project

        val moduleIdOption = SubpluginOption(
            key = "moduleId",
            value = "${kotlinCompilation.name}_${kotlinCompilation.target.name}_${project.fullName}",
        )

        val kspTask = kotlinCompilation.compileKotlinTaskName.replace("compile", "ksp")
        moduleIds[kspTask] = moduleIdOption.value

        val krpcDirectDependenciesOption = SubpluginOption(
            key = "krpcDirectDependencies",
            value = "",
        )

        return project.provider { listOf(moduleIdOption, krpcDirectDependenciesOption) }
    }

    @Suppress("RecursivePropertyAccessor")
    private val Project.fullName: String get() = (parent?.fullName?.let { "${it}_" } ?: "") + name
        .replace('.', '_')
        .replace('-', '_')

    override fun getCompilerPluginId(): String {
        return "org.jetbrains.krpc.krpc-compiler-plugin"
    }

    override fun getPluginArtifact(): SubpluginArtifact {
        return SubpluginArtifact(
            groupId = "org.jetbrains.krpc",
            artifactId = "krpc-compiler-plugin",
            version = "0.0.1",
        )
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
        return kotlinCompilation.target.project.plugins.hasPlugin(KRPCGradleKotlinCompilerPlugin::class.java)
    }
}
