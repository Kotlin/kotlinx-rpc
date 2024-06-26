/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.Project

fun Project.configureMetaTasks(vararg taskNames: String, excludeSubprojects: List<String> = emptyList()) {
    configureMetaTasks(taskNames.toList(), excludeSubprojects)
}

fun Project.configureMetaTasks(taskNames: List<String>, excludeSubprojects: List<String> = emptyList()) {
    val root = this
    val metaSet = taskNames.toSet()

    subprojects.forEach {
        if (it.name in excludeSubprojects) {
            return@forEach
        }

        it.tasks.all {
            val subtask = this

            if (subtask.name in metaSet) {
                val metaTask = root.tasks.findByName(subtask.name)
                    ?: root.task(subtask.name)

                metaTask.dependsOn(subtask)

                metaTask.group = "meta"
            }
        }
    }
}
