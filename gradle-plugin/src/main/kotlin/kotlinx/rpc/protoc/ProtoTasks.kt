/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc

import kotlinx.rpc.buf.tasks.BufExecTask
import kotlinx.rpc.buf.tasks.BufGenerateTask
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskCollection
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import java.util.function.IntFunction
import kotlin.reflect.KClass

/**
 * Returns a collection of all proto tasks registered in the project.
 */
public val Project.protoTasks: ProtoTasks<ProtoTask> get() = ProtoTasksImpl(this)

/**
 * Returns a collection of all `buf` tasks registered in the project.
 */
public val ProtoTasks<ProtoTask>.buf: ProtoTasks<BufExecTask> get() = matchingType(BufExecTask::class)

/**
 * Returns a collection of all `buf generate` tasks registered in the project.
 */
public val ProtoTasks<BufExecTask>.generate: ProtoTasks<BufGenerateTask> get() = matchingType(BufGenerateTask::class)

/**
 * Represents a collection of [ProtoTask] tasks of a given type.
 *
 * Allows for better filtering using additional method on top of Gradle's [TaskCollection].
 *
 * Example:
 * ```kotlin
 * protoTasks.matchingSourceSet("main")
 * protoTasks.testTasks()
 * protoTasks.
 *     .testTasks()
 *     .matching { ... }
 *     .all { ... }
 * ```
 */
public sealed interface ProtoTasks<ProtoTaskT : ProtoTask> : TaskCollection<ProtoTaskT> {
    /**
     * Filters tasks by type.
     *
     * ```kotlin
     * protoTasks.matchingType(BufGenerateTask::class)
     * ```
     */
    public fun <ProtoTaskT : ProtoTask> matchingType(kClass: KClass<ProtoTaskT>): ProtoTasks<ProtoTaskT>

    /**
     * Filters tasks by source set name.
     *
     * ```kotlin
     * protoTasks.matchingSourceSet("main")
     * ```
     */
    public fun matchingSourceSet(sourceSetName: String): ProtoTasks<ProtoTaskT>

    /**
     * Filters tasks by a Kotlin source set.
     *
     * ```kotlin
     * protoTasks.matchingKotlinSourceSet(kotlin.sourceSets.getByName("main"))
     * ```
     */
    public fun matchingKotlinSourceSet(sourceSet: KotlinSourceSet): ProtoTasks<ProtoTaskT>

    /**
     * Filters tasks by a Kotlin source set.
     *
     * ```kotlin
     * protoTasks.matchingKotlinSourceSet(kotlin.sourceSets.commonMain)
     * ```
     */
    public fun matchingKotlinSourceSet(sourceSet: NamedDomainObjectProvider<KotlinSourceSet>): ProtoTasks<ProtoTaskT>

    /**
     * Filters tasks by a source set.
     *
     * ```kotlin
     * protoTasks.matchingSourceSet(sourceSets.getByName("main")
     * ```
     */
    public fun matchingSourceSet(sourceSet: SourceSet): ProtoTasks<ProtoTaskT>

    /**
     * Filters tasks by a source set.
     *
     * ```kotlin
     * protoTasks.matchingSourceSet(sourceSets.main)
     * ```
     */
    public fun matchingSourceSet(sourceSet: NamedDomainObjectProvider<SourceSet>): ProtoTasks<ProtoTaskT>

    /**
     * Filters tasks by where [ProtoTask.Properties.isTest] is `true`.
     *
     * ```kotlin
     * rpc.protoc {
     *     buf.tasks.all().testTasks()
     * }
     * ```
     */
    public fun testTasks(): ProtoTasks<ProtoTaskT>

    /**
     * Filters tasks by where [ProtoTask.Properties.isTest] is `false`.
     *
     * ```kotlin
     * rpc.protoc {
     *     buf.tasks.all().nonTestTasks()
     * }
     * ```
     */
    public fun nonTestTasks(): ProtoTasks<ProtoTaskT>

    /**
     * Filters tasks by where [ProtoTask.AndroidProperties.isUnitTest] is `true`.
     *
     * ```kotlin
     * rpc.protoc {
     *     buf.tasks.all().androidUnitTestTasks()
     * }
     * ```
     */
    public fun androidUnitTestTasks(): ProtoTasks<ProtoTaskT>

    /**
     * Filters tasks by where [ProtoTask.AndroidProperties.isInstrumentedTest] is `true`.
     *
     * ```kotlin
     * rpc.protoc {
     *     buf.tasks.all().androidInstrumentedTestTasks()
     * }
     * ```
     */
    public fun androidInstrumentedTestTasks(): ProtoTasks<ProtoTaskT>

    /**
     * Filters tasks by where [ProtoTask.AndroidProperties.flavors] matches the given flavor.
     *
     * When `null` is passed, only variants without flavors are returned.
     *
     * Only returns Android tasks.
     *
     * ```kotlin
     * rpc.protoc {
     *     buf.tasks.all().matchingAndroidFlavor("freeApp")
     * }
     * ```
     */
    public fun matchingAndroidFlavor(flavor: String?): ProtoTasks<ProtoTaskT>

    /**
     * Filters tasks by where [ProtoTask.AndroidProperties.buildType] matches the given buildType.
     *
     * Only returns Android tasks.
     *
     * ```kotlin
     * rpc.protoc {
     *     buf.tasks.all().matchingAndroidBuildType("debug")
     * }
     * ```
     */
    public fun matchingAndroidBuildType(buildType: String?): ProtoTasks<ProtoTaskT>

    /**
     * Filters tasks by where [ProtoTask.AndroidProperties.variant] matches the given variant.
     *
     * Only returns Android tasks.
     *
     * ```kotlin
     * rpc.protoc {
     *     buf.tasks.all().matchingAndroidVariant("freeAppDebug")
     * }
     * ```
     */
    public fun matchingAndroidVariant(variant: String?): ProtoTasks<ProtoTaskT>
}


/**
 * Filters tasks by type.
 *
 * ```kotlin
 * rpc.protoc {
 *     buf.tasks.all().matchingType<BufGenerateTask>()
 * }
 * ```
 */
public inline fun <reified ProtoTaskT : ProtoTask> ProtoTasks<*>.matchingType(): ProtoTasks<ProtoTaskT> {
    return matchingType(ProtoTaskT::class)
}

internal inline fun <reified ProtoTaskT : ProtoTask> ProtoTasksImpl(project: Project): ProtoTasksImpl<ProtoTaskT> {
    return ProtoTasksImpl(project, project.tasks.withType(ProtoTaskT::class), ProtoTaskT::class)
}

internal open class ProtoTasksImpl<ProtoTaskT : ProtoTask>(
    private val project: Project,
    private val collection: TaskCollection<ProtoTaskT>,
    private val kClass: KClass<ProtoTaskT>,
) : ProtoTasks<ProtoTaskT>, TaskCollection<ProtoTaskT> by collection {
    override fun <Task : ProtoTask> matchingType(kClass: KClass<Task>): ProtoTasks<Task> {
        @Suppress("UNCHECKED_CAST")
        return ProtoTasksImpl(project, collection.matching { kClass.isInstance(it) } as TaskCollection<Task>, kClass)
    }

    override fun matchingSourceSet(sourceSetName: String): ProtoTasks<ProtoTaskT> {
        return ProtoTasksImpl(
            project,
            collection.matching { it.properties.sourceSetName == sourceSetName },
            kClass
        )
    }

    override fun matchingKotlinSourceSet(sourceSet: KotlinSourceSet): ProtoTasks<ProtoTaskT> {
        return matchingSourceSet(sourceSet.name)
    }

    override fun matchingKotlinSourceSet(sourceSet: NamedDomainObjectProvider<KotlinSourceSet>): ProtoTasks<ProtoTaskT> {
        return matchingSourceSet(sourceSet.name)
    }

    override fun matchingSourceSet(sourceSet: SourceSet): ProtoTasks<ProtoTaskT> {
        return matchingSourceSet(sourceSet.name)
    }

    override fun matchingSourceSet(sourceSet: NamedDomainObjectProvider<SourceSet>): ProtoTasks<ProtoTaskT> {
        return matchingSourceSet(sourceSet.name)
    }

    override fun testTasks(): ProtoTasks<ProtoTaskT> {
        return ProtoTasksImpl(project, collection.matching { it.properties.isTest }, kClass)
    }

    override fun nonTestTasks(): ProtoTasks<ProtoTaskT> {
        return ProtoTasksImpl(project, collection.matching { !it.properties.isTest }, kClass)
    }

    override fun androidUnitTestTasks(): ProtoTasks<ProtoTaskT> {
        return ProtoTasksImpl(
            project = project,
            collection = collection.matching {
                val properties = it.properties as? ProtoTask.AndroidProperties
                    ?: return@matching false

                properties.isUnitTest
            },
            kClass = kClass,
        )
    }

    override fun androidInstrumentedTestTasks(): ProtoTasks<ProtoTaskT> {
        return ProtoTasksImpl(
            project = project,
            collection = collection.matching {
                val properties = it.properties as? ProtoTask.AndroidProperties
                    ?: return@matching false

                properties.isInstrumentedTest
            },
            kClass = kClass,
        )
    }

    override fun matchingAndroidFlavor(flavor: String?): ProtoTasks<ProtoTaskT> {
        return ProtoTasksImpl(
            project = project,
            collection = collection.matching {
                val properties = it.properties as? ProtoTask.AndroidProperties
                    ?: return@matching false

                if (flavor == null) {
                    return@matching properties.flavors.isEmpty()
                }

                properties.flavors.contains(flavor)
            },
            kClass = kClass,
        )
    }

    override fun matchingAndroidBuildType(buildType: String?): ProtoTasks<ProtoTaskT> {
        return ProtoTasksImpl(
            project = project,
            collection = collection.matching {
                val properties = it.properties as? ProtoTask.AndroidProperties
                    ?: return@matching false

                properties.buildType == buildType
            },
            kClass = kClass,
        )
    }

    override fun matchingAndroidVariant(variant: String?): ProtoTasks<ProtoTaskT> {
        return ProtoTasksImpl(
            project = project,
            collection = collection.matching {
                val properties = it.properties as? ProtoTask.AndroidProperties
                    ?: return@matching false

                properties.variant == variant
            },
            kClass = kClass,
        )
    }

    fun empty() = ProtoTasksImpl(project, matching { false }, kClass)

    // Java default method override
    @Deprecated("Deprecated in Java")
    final override fun <T> toArray(generator: IntFunction<Array<out T?>?>): Array<out T?>? {
        @Suppress("DEPRECATION")
        return super<TaskCollection>.toArray(generator)
    }
}
