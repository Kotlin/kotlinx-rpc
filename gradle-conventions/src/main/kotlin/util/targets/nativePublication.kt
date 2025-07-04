/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.targets

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.plugins.PublishingPlugin
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family
import util.other.capitalized
import util.other.isPublicModule

private const val APPLE = "publishApplePublication"
private const val LINUX = "publishLinuxPublication"
private const val WINDOWS = "publishWindowsPublication"

fun Project.configureNativePublication(nativeTargets: List<KotlinTarget>) {
    if (!isPublicModule) {
        return
    }

    val grouped = nativeTargets.filterIsInstance<KotlinNativeTarget>().groupBy {
        val family = it.konanTarget.family

        when {
            family.isAppleFamily -> {
                APPLE
            }

            family == Family.LINUX -> {
                LINUX
            }

            family == Family.MINGW -> {
                WINDOWS
            }

            else -> null
        }
    }

    configureNativePublication(grouped, APPLE)
    configureNativePublication(grouped, LINUX)
    configureNativePublication(grouped, WINDOWS)
}

private fun Project.configureNativePublication(grouped: Map<String?, List<KotlinNativeTarget>>, groupName: String) {
    val targets = grouped[groupName] ?: return

    val repos = provider {
        the<PublishingExtension>().repositories.map { it.name }
    }

    repos.get().forEach { repositoryName ->
        tasks.register("${groupName}To${repositoryName.capitalized()}Repository").configure {
            group = PublishingPlugin.PUBLISH_TASK_GROUP

            dependsOn(targets.map {
                "publish${it.name.capitalized()}PublicationTo${repositoryName.capitalized()}Repository"
            })
        }
    }
}
