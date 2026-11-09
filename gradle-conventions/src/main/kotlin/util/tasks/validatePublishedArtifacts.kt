/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.gradle.work.DisableCachingByDefault
import java.io.File
import kotlin.collections.forEach
import kotlin.collections.ifEmpty
import kotlin.collections.toSortedSet

/**
 * Compares configured Maven publication descriptors with `gradle/artifacts/<selection>.txt`.
 * Set `kotlinx.rpc.artifacts.publicationTask` to one publication task name or absolute task path.
 * The task doesn't build or publish artifacts.
 *
 * Examples:
 * ```
 * ./gradlew validatePublishedArtifacts -Pkotlinx.rpc.artifacts.publicationTask=publishJsPublicationToBuildRepoRepository
 * ./gradlew validatePublishedArtifacts --dump -Pkotlinx.rpc.artifacts.publicationTask=:core:publishJvmPublicationToBuildRepoRepository
 * ```
 */
@DisableCachingByDefault(because = "Isn't worth caching")
internal abstract class ValidatePublishedArtifactsTask : DefaultTask() {

    @get:Option(option = DUMP_OPTION, description = "Dumps the list of published artifacts to a file")
    @get:Input
    abstract val dump: Property<Boolean>

    @get:Input
    protected abstract val publishedArtifacts: ListProperty<String>

    @get:Internal
    protected abstract val publishTaskName: Property<String>

    @get:OutputFile
    @get:Optional
    protected abstract val artifactsDump: RegularFileProperty

    init {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        description = "Checks that the list of published artifacts matches the expected one"

        dump.convention(false)
        publishedArtifacts.convention(emptyList())

        publishTaskName.convention(project.providers.gradleProperty(PUBLICATION_TASK_PROPERTY))
        artifactsDump.convention(project.layout.projectDirectory.file(publishTaskName.map { taskName ->
            "gradle/artifacts/${taskName.replace(":", "_")}.txt"
        }))
    }

    fun collectPublications(publishedProjects: List<Project>) {
        val taskName = publishTaskName.orNull ?: return
        val publishTasks = if (taskName.startsWith(":")) {
            listOf(project.tasks.getByPath(taskName))
        } else {
            publishedProjects.mapNotNull { it.tasks.findByName(taskName) }
        }
        if (publishTasks.isEmpty()) {
            throw GradleException("No publication tasks match '$taskName'")
        }

        // Read descriptors during configuration. File providers can carry artifact producer dependencies.
        val descriptors = publishTasks.flatMap { task ->
            when (task) {
                is PublishToMavenRepository -> task.publication.formatArtifacts()
                is PublicationGroupTask -> {
                    val publications = task.project.extensions.getByType(PublishingExtension::class.java).publications
                    task.publicationNames.get().flatMap { name ->
                        val publication = publications.findByName(name) as? MavenPublication
                            ?: throw GradleException(
                                "Publication '$name' selected by '${task.path}' is unavailable on this host. " +
                                    "Run complete artifact inventory validation on macOS."
                            )
                        publication.formatArtifacts()
                    }
                }
                else -> throw GradleException("Task '${task.path}' is not a Maven publication task or publication group")
            }
        }
        publishedArtifacts.set(descriptors)
    }

    @TaskAction
    fun runValidation() {
        if (!publishTaskName.isPresent) {
            throw GradleException("Set -P$PUBLICATION_TASK_PROPERTY=<publication task name or absolute task path>")
        }
        publishedArtifacts.finalizeValue()

        val actualArtifacts = publishedArtifacts.get().toSortedSet()
        val dumpFile = artifactsDump.get().asFile

        if (dump.get()) {
            if (!dumpFile.exists()) {
                dumpFile.parentFile.mkdirs()
                dumpFile.createNewFile()
            }

            dumpFile.bufferedWriter().use { writer ->
                actualArtifacts.forEach { artifact -> writer.appendLine(artifact) }
            }
            return
        }

        if (!dumpFile.exists()) reportDumpFileMissing(dumpFile)
        val expectedArtifacts = dumpFile.readLines().toSet()
        if (expectedArtifacts != actualArtifacts) reportInconsistentArtifacts(expectedArtifacts, actualArtifacts)
    }

    private fun reportDumpFileMissing(dumpFile: File): Nothing {
        throw GradleException("Expected artifacts dump file doesn't exist: $dumpFile\n$SOLUTION_DUMP")
    }

    private fun reportInconsistentArtifacts(expectedArtifacts: Set<String>, actualArtifacts: Set<String>): Nothing {
        val missingArtifacts = expectedArtifacts - actualArtifacts
        val extraArtifacts = actualArtifacts - expectedArtifacts
        val message = buildString {
            appendLine("The published artifacts differ from the expected ones")
            if (missingArtifacts.isNotEmpty()) {
                appendLine("  Expected artifacts are missing:")
                missingArtifacts.forEach { artifact -> appendLine("  - $artifact") }
            }
            if (extraArtifacts.isNotEmpty()) {
                appendLine("  Unexpected artifacts are found:")
                extraArtifacts.forEach { artifact -> appendLine("  - $artifact") }
            }
            appendLine(SOLUTION_DUMP)
        }
        throw GradleException(message)
    }

    companion object {
        const val NAME = "validatePublishedArtifacts"
        const val PUBLICATION_TASK_PROPERTY = "kotlinx.rpc.artifacts.publicationTask"

        private const val DUMP_OPTION = "dump"
        private const val SOLUTION_DUMP = "To save current list of artifacts as expected, run '$NAME --$DUMP_OPTION -P$PUBLICATION_TASK_PROPERTY=<selection>'"
    }
}

private fun MavenPublication.formatArtifacts(): List<String> =
    artifacts.map { "${groupId}:${artifactId}/${it.classifier.orEmpty()}.${it.extension}" }
        .ifEmpty { listOf("$groupId:$artifactId") }
