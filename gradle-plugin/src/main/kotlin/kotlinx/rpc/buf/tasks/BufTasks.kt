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

public sealed interface BufTasks<BufTask : BufExecTask> : TaskCollection<BufTask> {
    public fun matchingSourceSet(sourceSetName: String): BufTasks<BufTask>

    public fun matchingKotlinSourceSet(sourceSet: KotlinSourceSet): BufTasks<BufTask>

    public fun matchingKotlinSourceSet(sourceSet: NamedDomainObjectProvider<KotlinSourceSet>): BufTasks<BufTask>

    public fun matchingSourceSet(sourceSet: SourceSet): BufTasks<BufTask>

    public fun matchingSourceSet(sourceSet: NamedDomainObjectProvider<SourceSet>): BufTasks<BufTask>

    public fun executedForSourceSet(sourceSetName: String): BufTasks<BufTask>

    public fun executedForSourceSet(sourceSet: KotlinSourceSet): BufTasks<BufTask>

    public fun executedForSourceSet(sourceSet: SourceSet): BufTasks<BufTask>

    public fun testTasks(): BufTasks<BufTask>

    public fun nonTestTasks(): BufTasks<BufTask>

    // android

    public fun matchingFlavor(flavor: String): BufTasks<BufTask>

    public fun matchingBuildType(buildType: String): BufTasks<BufTask>

    public fun matchingVariant(variant: String): BufTasks<BufTask>
}

public sealed interface BufAllTasks : BufTasks<BufExecTask> {
    public fun <BufTask : BufExecTask> matchingType(kClass: KClass<BufTask>): BufTasks<BufTask>
}

public inline fun <reified BufTask : BufExecTask> BufAllTasks.matchingType(): BufTasks<BufTask> {
    return matchingType(BufTask::class)
}

public inline fun <reified BufTask : BufExecTask> BufTask.bufDependsOn(): BufTasks<BufTask> {
    return bufDependsOn(BufTask::class)
}

@PublishedApi
internal fun <BufTask : BufExecTask> BufTask.bufDependsOn(kClass: KClass<BufTask>): BufTasks<BufTask> {
    return BufTasksImpl(project, project.tasks.withType(kClass).matching { it.name in bufTaskDependencies.get() }, kClass)
}

internal open class BufTasksImpl<BufTask : BufExecTask> internal constructor(
    private val project: Project,
    private val collection: TaskCollection<BufTask>,
    private val kClass: KClass<BufTask>,
) : BufTasks<BufTask>, TaskCollection<BufTask> by collection {
    override fun matchingSourceSet(sourceSetName: String): BufTasks<BufTask> {
        return BufTasksImpl(
            project,
            collection.matching { it.properties.sourceSetName == sourceSetName },
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
            it.properties.sourceSetName == sourceSetName
        }.singleOrNull()?.bufDependsOn(kClass) ?: return empty()

        val allExecutedLazySet = lazy { allExecuted.map { it.name }.toSet() }

        return BufTasksImpl(
            project = project,
            collection = collection.matching { dependency ->
                dependency.properties.sourceSetName == sourceSetName || dependency.name in allExecutedLazySet.value
            },
            kClass = kClass,
        )
    }

    override fun executedForSourceSet(sourceSet: KotlinSourceSet): BufTasks<BufTask> {
        return executedForSourceSet(sourceSet.name)
    }

    override fun executedForSourceSet(sourceSet: SourceSet): BufTasks<BufTask> {
        return executedForSourceSet(sourceSet.name)
    }

    override fun testTasks(): BufTasks<BufTask> {
        return BufTasksImpl(project, collection.matching { it.properties.isTest }, kClass)
    }

    override fun nonTestTasks(): BufTasks<BufTask> {
        return BufTasksImpl(project, collection.matching { !it.properties.isTest }, kClass)
    }

    override fun matchingFlavor(flavor: String): BufTasks<BufTask> {
        return BufTasksImpl(
            project = project,
            collection = collection.matching {
                (it.properties as? BufExecTask.AndroidProperties)?.flavour == flavor
            },
            kClass = kClass,
        )
    }

    override fun matchingBuildType(buildType: String): BufTasks<BufTask> {
        return BufTasksImpl(
            project = project,
            collection = collection.matching {
                (it.properties as? BufExecTask.AndroidProperties)?.buildType == buildType
            },
            kClass = kClass,
        )
    }

    override fun matchingVariant(variant: String): BufTasks<BufTask> {
        return BufTasksImpl(
            project = project,
            collection = collection.matching {
                (it.properties as? BufExecTask.AndroidProperties)?.variant == variant
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
