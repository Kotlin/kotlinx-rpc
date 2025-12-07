/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.buf.tasks

import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskCollection
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import java.util.function.IntFunction
import kotlin.reflect.KClass

/**
 * Represents a collection of buf tasks of a given type.
 *
 * Allows for better filtering using additional method on top of Gradle's [TaskCollection].
 *
 * Example:
 * ```kotlin
 * rpc.protoc {
 *     buf.tasks.all().matchingSourceSet("main")
 *     buf.tasks.all().testTasks()
 *     buf.tasks.all()
 *         .testTasks()
 *         .matching { ... }
 *         .all { ... }
 * }
 * ```
 */
public sealed interface BufTasks<BufTask : BufExecTask> : TaskCollection<BufTask> {
    /**
     * Filters tasks by source set name.
     *
     * ```kotlin
     * rpc.protoc {
     *     buf.tasks.all().matchingSourceSet("main")
     * }
     * ```
     */
    public fun matchingSourceSet(sourceSetName: String): BufTasks<BufTask>

    /**
     * Filters tasks by a Kotlin source set.
     *
     * ```kotlin
     * rpc.protoc {
     *     buf.tasks.all().matchingSourceSet(kotlin.sourceSets.getByName("main"))
     * }
     * ```
     */
    public fun matchingKotlinSourceSet(sourceSet: KotlinSourceSet): BufTasks<BufTask>

    /**
     * Filters tasks by a Kotlin source set.
     *
     * ```kotlin
     * rpc.protoc {
     *     buf.tasks.all().matchingSourceSet(kotlin.sourceSets.commonMain)
     * }
     * ```
     */
    public fun matchingKotlinSourceSet(sourceSet: NamedDomainObjectProvider<KotlinSourceSet>): BufTasks<BufTask>

    /**
     * Filters tasks by a source set.
     *
     * ```kotlin
     * rpc.protoc {
     *     buf.tasks.all().matchingSourceSet(sourceSets.getByName("main"))
     * }
     * ```
     */
    public fun matchingSourceSet(sourceSet: SourceSet): BufTasks<BufTask>

    /**
     * Filters tasks by a source set.
     *
     * ```kotlin
     * rpc.protoc {
     *     buf.tasks.all().matchingSourceSet(sourceSets.main)
     * }
     * ```
     */
    public fun matchingSourceSet(sourceSet: NamedDomainObjectProvider<SourceSet>): BufTasks<BufTask>

    /**
     * Returns a collection of all buf tasks of the given type [BufTask] that will be executed for the given source set.
     *
     * This functionality uses [KotlinSourceSet.dependsOn] and inherits it's limitations:
     * > Note that the Kotlin Gradle plugin may add additional required source sets
     * on late stages of Gradle configuration
     * and the most reliable way to get a full final set is to use this property
     * as a task input with [org.gradle.api.provider.Provider] type.
     *
     * Unlike [KotlinSourceSet.dependsOn], this method also considers the `test -> main` dependency.
     *
     * Correct example:
     * ```kotlin
     * // use bufTaskNames in other tasks as in input, e.g.
     * //
     * // returns bufGenerateCommonMain, bufGenerateNativeMain, bufGenerateAppleMain
     * // in default Kotlin source set hierarchy
     * val bufTaskNames = project.provider {
     *     rpc.protoc.get()
     *         .buf.tasks.all()
     *             .executedForSourceSet("bufGenerateAppleMain")
     *             .map { it.name }
     * }
     * ```
     *
     * Incorrect example that will print only `bufGenerateAppleMain`,
     * because [KotlinSourceSet.dependsOn] won't yet be resolved:
     * ```kotlin
     * rpc.protoc {
     *     buf.tasks.all()
     *         .executedForSourceSet("bufGenerateAppleMain")
     *         .all { println(it.name) }
     * }
     * ```
     *
     * Mind the difference with [bufDependsOn]:
     * [executedForSourceSet] also includes the task for the given source set.
     *
     * For the correct example:
     * - [bufDependsOn] returns `bufGenerateCommonMain` and `bufGenerateNativeMain`
     * - [executedForSourceSet] returns `bufGenerateCommonMain`, `bufGenerateNativeMain` and `bufGenerateAppleMain`
     */
    public fun executedForSourceSet(sourceSetName: String): BufTasks<BufTask>

    /**
     * Returns a collection of all buf tasks of the given type [BufTask] that will be executed for the given source set.
     *
     * This functionality uses [KotlinSourceSet.dependsOn] and inherits it's limitations:
     * > Note that the Kotlin Gradle plugin may add additional required source sets
     * on late stages of Gradle configuration
     * and the most reliable way to get a full final set is to use this property
     * as a task input with [org.gradle.api.provider.Provider] type.
     *
     * Unlike [KotlinSourceSet.dependsOn], this method also considers the `test -> main` dependency.
     *
     * Correct example:
     * ```kotlin
     * // use bufTaskNames in other tasks as in input, e.g.
     * //
     * // returns bufGenerateCommonMain, bufGenerateNativeMain, bufGenerateAppleMain
     * // in default Kotlin source set hierarchy
     * val bufTaskNames = project.provider {
     *     rpc.protoc.get()
     *         .buf.tasks.all()
     *             .executedForSourceSet(kotlin.sourceSets.appleMain.get())
     *             .map { it.name }
     * }
     * ```
     *
     * Incorrect example that will print only `bufGenerateAppleMain`,
     * because [KotlinSourceSet.dependsOn] won't yet be resolved:
     * ```kotlin
     * rpc.protoc {
     *     buf.tasks.all()
     *         .executedForSourceSet(kotlin.sourceSets.appleMain.get())
     *         .all { println(it.name) }
     * }
     * ```
     *
     * Mind the difference with [bufDependsOn]:
     * [executedForKotlinSourceSet] also includes the task for the given source set.
     *
     * For the correct example:
     * - [bufDependsOn] returns `bufGenerateCommonMain` and `bufGenerateNativeMain`
     * - [executedForKotlinSourceSet] returns `bufGenerateCommonMain`, `bufGenerateNativeMain` and `bufGenerateAppleMain`
     */
    public fun executedForKotlinSourceSet(sourceSet: KotlinSourceSet): BufTasks<BufTask>

    /**
     * Returns a collection of all buf tasks of the given type [BufTask] that will be executed for the given source set.
     *
     * This functionality uses [KotlinSourceSet.dependsOn] and inherits it's limitations:
     * > Note that the Kotlin Gradle plugin may add additional required source sets
     * on late stages of Gradle configuration
     * and the most reliable way to get a full final set is to use this property
     * as a task input with [org.gradle.api.provider.Provider] type.
     *
     * Unlike [KotlinSourceSet.dependsOn], this method also considers the `test -> main` dependency.
     *
     * Correct example:
     * ```kotlin
     * // use bufTaskNames in other tasks as in input, e.g.
     * //
     * // returns bufGenerateCommonMain, bufGenerateNativeMain, bufGenerateAppleMain
     * // in default Kotlin source set hierarchy
     * val bufTaskNames = project.provider {
     *     rpc.protoc.get()
     *         .buf.tasks.all()
     *             .executedForSourceSet(kotlin.sourceSets.appleMain)
     *             .map { it.name }
     * }
     * ```
     *
     * Incorrect example that will print only `bufGenerateAppleMain`,
     * because [KotlinSourceSet.dependsOn] won't yet be resolved:
     * ```kotlin
     * rpc.protoc {
     *     buf.tasks.all()
     *         .executedForSourceSet(kotlin.sourceSets.appleMain)
     *         .all { println(it.name) }
     * }
     * ```
     *
     * Mind the difference with [bufDependsOn]:
     * [executedForKotlinSourceSet] also includes the task for the given source set.
     *
     * For the correct example:
     * - [bufDependsOn] returns `bufGenerateCommonMain` and `bufGenerateNativeMain`
     * - [executedForKotlinSourceSet] returns `bufGenerateCommonMain`, `bufGenerateNativeMain` and `bufGenerateAppleMain`
     */
    public fun executedForKotlinSourceSet(sourceSet: NamedDomainObjectProvider<KotlinSourceSet>): BufTasks<BufTask>

    /**
     * Returns a collection of all buf tasks of the given type [BufTask] that will be executed for the given source set.
     *
     * This functionality uses [KotlinSourceSet.dependsOn] and inherits it's limitations:
     * > Note that the Kotlin Gradle plugin may add additional required source sets
     * on late stages of Gradle configuration
     * and the most reliable way to get a full final set is to use this property
     * as a task input with [org.gradle.api.provider.Provider] type.
     *
     * Unlike [KotlinSourceSet.dependsOn], this method also considers the `test -> main` dependency.
     *
     * Correct example:
     * ```kotlin
     * // use bufTaskNames in other tasks as in input, e.g.
     * //
     * // returns bufGenerateMain, bufGenerateTest
     * // in default Kotlin source set hierarchy
     * val bufTaskNames = project.provider {
     *     rpc.protoc.get()
     *         .buf.tasks.all()
     *             .executedForSourceSet(sourceSets.test.get())
     *             .map { it.name }
     * }
     * ```
     *
     * Incorrect example.
     * Although, the result will be correct in Kotlin/JVM projects (will print`bufGenerateMain`),
     * this is a bad pattern because [KotlinSourceSet.dependsOn] won't yet be resolved:
     * ```kotlin
     * rpc.protoc {
     *     buf.tasks.all()
     *         .executedForSourceSet(sourceSets.test.get())
     *         .all { println(it.name) }
     * }
     * ```
     *
     * Mind the difference with [bufDependsOn]:
     * [executedForSourceSet] also includes the task for the given source set.
     *
     * For the correct example:
     * - [bufDependsOn] returns `bufGenerateMain`
     * - [executedForSourceSet] returns `bufGenerateMain` and `bufGenerateTest`
     */
    public fun executedForSourceSet(sourceSet: SourceSet): BufTasks<BufTask>

    /**
     * Returns a collection of all buf tasks of the given type [BufTask] that will be executed for the given source set.
     *
     * This functionality uses [KotlinSourceSet.dependsOn] and inherits it's limitations:
     * > Note that the Kotlin Gradle plugin may add additional required source sets
     * on late stages of Gradle configuration
     * and the most reliable way to get a full final set is to use this property
     * as a task input with [org.gradle.api.provider.Provider] type.
     *
     * Unlike [KotlinSourceSet.dependsOn], this method also considers the `test -> main` dependency.
     *
     * Correct example:
     * ```kotlin
     * // use bufTaskNames in other tasks as in input, e.g.
     * //
     * // returns bufGenerateMain, bufGenerateTest
     * // in default Kotlin source set hierarchy
     * val bufTaskNames = project.provider {
     *     rpc.protoc.get()
     *         .buf.tasks.all()
     *             .executedForSourceSet(sourceSets.test)
     *             .map { it.name }
     * }
     * ```
     *
     * Incorrect example.
     * Although, the result will be correct in Kotlin/JVM projects (will print`bufGenerateMain`),
     * this is a bad pattern because [KotlinSourceSet.dependsOn] won't yet be resolved:
     * ```kotlin
     * rpc.protoc {
     *     buf.tasks.all()
     *         .executedForSourceSet(sourceSets.test)
     *         .all { println(it.name) }
     * }
     * ```
     *
     * Mind the difference with [bufDependsOn]:
     * [executedForSourceSet] also includes the task for the given source set.
     *
     * For the correct example:
     * - [bufDependsOn] returns `bufGenerateMain`
     * - [executedForSourceSet] returns `bufGenerateMain` and `bufGenerateTest`
     */
    public fun executedForSourceSet(sourceSet: NamedDomainObjectProvider<SourceSet>): BufTasks<BufTask>

    /**
     * Filters tasks by where [BufExecTask.Properties.isTest] is `true`.
     *
     * ```kotlin
     * rpc.protoc {
     *     buf.tasks.all().testTasks()
     * }
     * ```
     */
    public fun testTasks(): BufTasks<BufTask>

    /**
     * Filters tasks by where [BufExecTask.Properties.isTest] is `false`.
     *
     * ```kotlin
     * rpc.protoc {
     *     buf.tasks.all().nonTestTasks()
     * }
     * ```
     */
    public fun nonTestTasks(): BufTasks<BufTask>

    /**
     * Filters tasks by where [BufExecTask.AndroidProperties.isUnitTest] is `true`.
     *
     * ```kotlin
     * rpc.protoc {
     *     buf.tasks.all().androidUnitTestTasks()
     * }
     * ```
     */
    public fun androidUnitTestTasks(): BufTasks<BufTask>

    /**
     * Filters tasks by where [BufExecTask.AndroidProperties.isInstrumentedTest] is `true`.
     *
     * ```kotlin
     * rpc.protoc {
     *     buf.tasks.all().androidInstrumentedTestTasks()
     * }
     * ```
     */
    public fun androidInstrumentedTestTasks(): BufTasks<BufTask>

    /**
     * Filters tasks by where [BufExecTask.AndroidProperties.flavors] matches the given flavor.
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
    public fun matchingAndroidFlavor(flavor: String?): BufTasks<BufTask>

    /**
     * Filters tasks by where [BufExecTask.AndroidProperties.buildType] matches the given buildType.
     *
     * Only returns Android tasks.
     *
     * ```kotlin
     * rpc.protoc {
     *     buf.tasks.all().matchingAndroidBuildType("debug")
     * }
     * ```
     */
    public fun matchingAndroidBuildType(buildType: String?): BufTasks<BufTask>

    /**
     * Filters tasks by where [BufExecTask.AndroidProperties.variant] matches the given variant.
     *
     * Only returns Android tasks.
     *
     * ```kotlin
     * rpc.protoc {
     *     buf.tasks.all().matchingAndroidVariant("freeAppDebug")
     * }
     * ```
     */
    public fun matchingAndroidVariant(variant: String?): BufTasks<BufTask>
}

/**
 * A version of [BufTasks] that contains all buf tasks and allows filtering by type.
 *
 * ```kotlin
 * rpc.protoc {
 *     buf.tasks.all().matchingType<BufGenerateTask>()
 * }
 * ```
 */
public sealed interface BufAllTasks : BufTasks<BufExecTask> {
    /**
     * Filters tasks by type.
     *
     * ```kotlin
     * rpc.protoc {
     *     buf.tasks.all().matchingType(BufGenerateTask::class)
     * }
     * ```
     */
    public fun <BufTask : BufExecTask> matchingType(kClass: KClass<BufTask>): BufTasks<BufTask>
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
public inline fun <reified BufTask : BufExecTask> BufAllTasks.matchingType(): BufTasks<BufTask> {
    return matchingType(BufTask::class)
}

/**
 * Returns a collection of all buf tasks of the given type [BufTask] that this task depends on.
 *
 * This functionality uses [KotlinSourceSet.dependsOn] and inherits it's limitations:
 * > Note that the Kotlin Gradle plugin may add additional required source sets
 * on late stages of Gradle configuration
 * and the most reliable way to get a full final set is to use this property
 * as a task input with [org.gradle.api.provider.Provider] type.
 *
 * Unlike [KotlinSourceSet.dependsOn], this method also considers the `test -> main` dependency.
 *
 * Correct example:
 * ```kotlin
 * // use bufTaskNames in other tasks as in input, e.g.
 * //
 * // returns bufGenerateCommonMain, bufGenerateNativeMain
 * // in default Kotlin source set hierarchy
 * val bufTaskNames = project.provider {
 *     rpc.protoc.get()
 *         .buf.tasks.all()
 *             .getByName("bufGenerateAppleMain")
 *             .bufDependsOn()
 *             .map { it.name }
 * }
 * ```
 *
 * Incorrect example that will print nothing, because [KotlinSourceSet.dependsOn] won't yet be resolved:
 * ```kotlin
 * rpc.protoc {
 *     buf.tasks.all()
 *         .getByName("bufGenerateAppleMain")
 *         .bufDependsOn()
 *         .all { println(it.name) }
 * }
 * ```
 *
 * Mind the difference with [BufTasks.executedForSourceSet]:
 * [bufDependsOn] doesn't include the task for the given source set.
 *
 * For the correct example:
 * - [bufDependsOn] returns `bufGenerateCommonMain` and `bufGenerateNativeMain`
 * - [BufTasks.executedForSourceSet] returns `bufGenerateCommonMain`, `bufGenerateNativeMain` and `bufGenerateAppleMain`
 */
public inline fun <reified BufTask : BufExecTask> BufTask.bufDependsOn(): BufTasks<BufTask> {
    return bufDependsOn(BufTask::class)
}

@PublishedApi
internal fun <BufTask : BufExecTask> BufTask.bufDependsOn(kClass: KClass<BufTask>): BufTasks<BufTask> {
    return BufTasksImpl(
        project,
        project.tasks.withType(kClass).matching { it.name in bufTaskDependencies.get() },
        kClass
    )
}

internal open class BufTasksImpl<BufTask : BufExecTask> internal constructor(
    private val project: Project,
    private val collection: TaskCollection<BufTask>,
    private val kClass: KClass<BufTask>,
) : BufTasks<BufTask>, TaskCollection<BufTask> by collection {
    override fun matchingSourceSet(sourceSetName: String): BufTasks<BufTask> {
        return BufTasksImpl(
            project,
            collection.matching { it.properties.get().sourceSetName == sourceSetName },
            kClass
        )
    }

    override fun matchingKotlinSourceSet(sourceSet: KotlinSourceSet): BufTasks<BufTask> {
        return matchingSourceSet(sourceSet.name)
    }

    override fun matchingKotlinSourceSet(sourceSet: NamedDomainObjectProvider<KotlinSourceSet>): BufTasks<BufTask> {
        return matchingSourceSet(sourceSet.name)
    }

    override fun matchingSourceSet(sourceSet: SourceSet): BufTasks<BufTask> {
        return matchingSourceSet(sourceSet.name)
    }

    override fun matchingSourceSet(sourceSet: NamedDomainObjectProvider<SourceSet>): BufTasks<BufTask> {
        return matchingSourceSet(sourceSet.name)
    }

    override fun executedForSourceSet(sourceSetName: String): BufTasks<BufTask> {
        val allExecuted = project.tasks.withType(kClass.java).matching {
            it.properties.get().sourceSetName == sourceSetName
        }.singleOrNull()?.bufDependsOn(kClass) ?: return empty()

        val allExecutedLazySet = lazy { allExecuted.map { it.name }.toSet() }

        return BufTasksImpl(
            project = project,
            collection = collection.matching { dependency ->
                dependency.properties.get().sourceSetName == sourceSetName || dependency.name in allExecutedLazySet.value
            },
            kClass = kClass,
        )
    }

    override fun executedForKotlinSourceSet(sourceSet: KotlinSourceSet): BufTasks<BufTask> {
        return executedForSourceSet(sourceSet.name)
    }

    override fun executedForKotlinSourceSet(sourceSet: NamedDomainObjectProvider<KotlinSourceSet>): BufTasks<BufTask> {
        return executedForSourceSet(sourceSet.name)
    }

    override fun executedForSourceSet(sourceSet: SourceSet): BufTasks<BufTask> {
        return executedForSourceSet(sourceSet.name)
    }

    override fun executedForSourceSet(sourceSet: NamedDomainObjectProvider<SourceSet>): BufTasks<BufTask> {
        return executedForSourceSet(sourceSet.name)
    }

    override fun testTasks(): BufTasks<BufTask> {
        return BufTasksImpl(project, collection.matching { it.properties.get().isTest }, kClass)
    }

    override fun nonTestTasks(): BufTasks<BufTask> {
        return BufTasksImpl(project, collection.matching { !it.properties.get().isTest }, kClass)
    }

    override fun androidUnitTestTasks(): BufTasks<BufTask> {
        return BufTasksImpl(
            project = project,
            collection = collection.matching {
                val properties = it.properties.get() as? BufExecTask.AndroidProperties
                    ?: return@matching false

                properties.isUnitTest
            },
            kClass = kClass,
        )
    }

    override fun androidInstrumentedTestTasks(): BufTasks<BufTask> {
        return BufTasksImpl(
            project = project,
            collection = collection.matching {
                val properties = it.properties.get() as? BufExecTask.AndroidProperties
                    ?: return@matching false

                properties.isInstrumentedTest
            },
            kClass = kClass,
        )
    }

    override fun matchingAndroidFlavor(flavor: String?): BufTasks<BufTask> {
        return BufTasksImpl(
            project = project,
            collection = collection.matching {
                val properties = it.properties.get() as? BufExecTask.AndroidProperties
                    ?: return@matching false

                if (flavor == null) {
                    return@matching properties.flavors.isEmpty()
                }

                properties.flavors.contains(flavor)
            },
            kClass = kClass,
        )
    }

    override fun matchingAndroidBuildType(buildType: String?): BufTasks<BufTask> {
        return BufTasksImpl(
            project = project,
            collection = collection.matching {
                val properties = it.properties.get() as? BufExecTask.AndroidProperties
                    ?: return@matching false

                properties.buildType == buildType
            },
            kClass = kClass,
        )
    }

    override fun matchingAndroidVariant(variant: String?): BufTasks<BufTask> {
        return BufTasksImpl(
            project = project,
            collection = collection.matching {
                val properties = it.properties.get() as? BufExecTask.AndroidProperties
                    ?: return@matching false

                properties.variant == variant
            },
            kClass = kClass,
        )
    }

    // Java default method override
    @Deprecated("Deprecated in Java")
    final override fun <T> toArray(generator: IntFunction<Array<out T?>?>): Array<out T?>? {
        @Suppress("DEPRECATION")
        return super<TaskCollection>.toArray(generator)
    }

    fun empty() = BufTasksImpl(project, matching { false }, kClass)
}

internal open class BufAllTasksImpl internal constructor(
    private val project: Project,
    private val collection: BufTasksImpl<BufExecTask>,
) : BufAllTasks, BufTasks<BufExecTask> by collection {
    constructor(project: Project, collection: TaskCollection<BufExecTask>) : this(
        project,
        BufTasksImpl(project, collection, BufExecTask::class)
    )

    override fun <Task : BufExecTask> matchingType(kClass: KClass<Task>): BufTasks<Task> {
        @Suppress("UNCHECKED_CAST")
        return BufTasksImpl(project, collection.matching { kClass.isInstance(it) } as TaskCollection<Task>, kClass)
    }

    // Java default method override
    @Deprecated("Deprecated in Java")
    final override fun <T> toArray(generator: IntFunction<Array<out T?>?>): Array<out T?>? {
        @Suppress("DEPRECATION")
        return super<BufTasks>.toArray(generator)
    }
}
