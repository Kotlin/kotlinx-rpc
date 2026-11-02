/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc

import kotlinx.rpc.internal.InternalForSubclassingRpcApi
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.Internal
import org.gradle.work.DisableCachingByDefault
import org.jetbrains.kotlinx.library.api.watchdog.ExemptionReason
import org.jetbrains.kotlinx.library.api.watchdog.IntentionallyOpen
import org.jetbrains.kotlinx.library.api.watchdog.IntentionallyWithoutEqualsHashCodeOrToString

/**
 * Abstract base interface for tasks that work with .proto files.
 */
@IntentionallyOpen(reason = ExemptionReason.API_DESIGN)
public interface ProtoTask : Task {
    @get:Internal
    public val properties: Properties

    /**
     * Properties of the buf task.
     *
     * Can be used with [ProtoTasks] to filter tasks.
     */
    @SubclassOptInRequired(InternalForSubclassingRpcApi::class)
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
         * For Kotlin/Multiplatform it also has only one source set
         * (`commonMain`, `commonTest`, or `jsMain`, for example).
         *
         * For Android, Kotlin/Android, and Kotlin/Multiplatform + Android it can have multiple source sets:
         * - `["androidMain", "androidDebug", "main", "debug"]`
         * - `["androidTest", "androidUnitTestDebug", "test", "testDebug"]`
         */
        public val sourceSetNames: Set<String>,
    ) {
        override fun equals(other: Any?): Boolean {
            return isTest == (other as? Properties)?.isTest && sourceSetNames == other.sourceSetNames
        }

        override fun hashCode(): Int {
            return isTest.hashCode() * 31 + sourceSetNames.hashCode()
        }

        override fun toString(): String {
            return "Properties(isTest=$isTest, sourceSetNames=$sourceSetNames)"
        }
    }

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
    ) : Properties(isTest, sourceSetNames) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as AndroidProperties

            if (isInstrumentedTest != other.isInstrumentedTest) return false
            if (isUnitTest != other.isUnitTest) return false
            if (flavors != other.flavors) return false
            if (buildType != other.buildType) return false
            if (variant != other.variant) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + isInstrumentedTest.hashCode()
            result = 31 * result + isUnitTest.hashCode()
            result = 31 * result + flavors.hashCode()
            result = 31 * result + buildType.hashCode()
            result = 31 * result + variant.hashCode()
            return result
        }

        override fun toString(): String {
            return "AndroidProperties(" +
                "flavors=$flavors, " +
                "buildType=$buildType, " +
                "variant=$variant, " +
                "isInstrumentedTest=$isInstrumentedTest, " +
                "isUnitTest=$isUnitTest" +
                ")"
        }
    }
}

/**
 * Default implementation of [ProtoTask] with [Task.group] set to [PROTO_GROUP].
 *
 * Caching is disabled at this level because the base class carries no declared inputs or outputs.
 * Subclasses must declare their caching intent explicitly via `@CacheableTask` or
 * `@DisableCachingByDefault` once they know what they produce.
 */
@IntentionallyOpen(reason = ExemptionReason.API_DESIGN)
@IntentionallyWithoutEqualsHashCodeOrToString(reason = ExemptionReason.API_DESIGN)
@DisableCachingByDefault(because = "Subclasses must declare caching intent explicitly")
public abstract class DefaultProtoTask(
    @get:Internal
    final override val properties: ProtoTask.Properties,
) : ProtoTask, DefaultTask() {
    init {
        group = PROTO_GROUP
    }
}
