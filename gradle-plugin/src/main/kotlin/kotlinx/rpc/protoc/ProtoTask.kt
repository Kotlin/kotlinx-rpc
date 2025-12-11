/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc

import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.Internal

/**
 * Abstract base interface for tasks that work with .proto files.
 */
public interface ProtoTask : Task {
    @get:Internal
    public val properties: Properties

    /**
     * Properties of the buf task.
     *
     * Can be used with [ProtoTasks] to filter tasks.
     */
    public open class Properties(
        /**
         * Whether the task is for a test source set.
         */
        public val isTest: Boolean,
        /**
         * Names of all [kotlinx.rpc.protoc.ProtoSourceSet]s this task is associated with.
         *
         * For Kotlin/JVM it has only one source set (`main`, or `test`, for example).
         *
         * For Kotlin/Multiplatform it also has only one source set (`commonMain`, `commonTest`, or `jsMain`, for example).
         *
         * For Android, Kotlin/Android, and Kotlin/Multiplatform + Android it can have multiple source sets:
         * - `["androidMain", "androidDebug", "main", "debug"]`
         * - `["androidTest", "androidUnitTestDebug", "test", "testDebug"]`
         */
        public val sourceSetNames: Set<String>,
    )

    /**
     * Properties of the buf task for android source sets.
     *
     * Can be used with [ProtoTasks] to filter tasks.
     */
    public class AndroidProperties(
        /**
         * @see Properties.isTest
         */
        isTest: Boolean,

        /**
         * @see Properties.sourceSetNames
         */
        sourceSetNames: Set<String>,

        /**
         * Name of the android flavors this task is associated with.
         *
         * Can be empty for 'com.android.kotlin.multiplatform.library' source sets.
         *
         * @see com.android.build.api.variant.Variant.productFlavors
         */
        public val flavors: List<String>,

        /**
         * Name of the android build type this task is associated with.
         *
         * @see com.android.build.api.variant.Variant.buildType
         */
        public val buildType: String?,

        /**
         * Name of the android variant this task is associated with.
         *
         * Can be `null` for 'com.android.kotlin.multiplatform.library' source sets.
         *
         * @see com.android.build.api.variant.Variant.name
         */
        public val variant: String?,

        /**
         * Whether the task is for instrumentation tests.
         */
        public val isInstrumentedTest: Boolean,

        /**
         * Whether the task is for unit tests.
         */
        public val isUnitTest: Boolean,
    ) : Properties(isTest, sourceSetNames)
}

/**
 * Default implementation of [ProtoTask] with [Task.group] set to [PROTO_GROUP].
 */
public abstract class DefaultProtoTask(
    @get:Internal
    final override val properties: ProtoTask.Properties,
) : ProtoTask, DefaultTask() {
    init {
        group = PROTO_GROUP
    }
}
