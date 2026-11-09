/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.publish.plugins.PublishingPlugin
import org.gradle.api.tasks.Internal
import org.gradle.kotlin.dsl.register
import org.gradle.work.DisableCachingByDefault
import util.other.capitalized

/** Shares publication names between aggregate publishing tasks and inventory validation. */
@DisableCachingByDefault(because = "Aggregates publication tasks")
abstract class PublicationGroupTask : DefaultTask() {
    @get:Internal
    abstract val publicationNames: ListProperty<String>

    init {
        publicationNames.convention(emptyList())
    }
}

fun Project.registerPublicationGroup(taskName: String, repositoryName: String, publications: List<String>) {
    tasks.register<PublicationGroupTask>(taskName) {
        group = PublishingPlugin.PUBLISH_TASK_GROUP
        publicationNames.set(publications)
        dependsOn(publicationNames.map { names ->
            names.map { "publish${it.capitalized()}PublicationTo${repositoryName.capitalized()}Repository" }
        })
    }
}
