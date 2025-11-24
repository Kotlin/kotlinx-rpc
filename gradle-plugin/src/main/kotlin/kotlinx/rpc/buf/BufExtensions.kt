/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.buf

import kotlinx.rpc.buf.tasks.BufAllTasks
import kotlinx.rpc.buf.tasks.BufAllTasksImpl
import kotlinx.rpc.buf.tasks.BufExecTask
import kotlinx.rpc.buf.tasks.BufGenerateTask
import kotlinx.rpc.buf.tasks.BufTasks
import kotlinx.rpc.buf.tasks.BufTasksImpl
import kotlinx.rpc.protoc.ProtocPlugin
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import java.io.File
import javax.inject.Inject
import kotlin.reflect.KClass
import kotlin.time.Duration

/**
 * Options for the Buf tasks.
 *
 * @see <a href="https://buf.build/docs/reference/cli/buf/">buf commands</a>
 */
public open class BufExtension @Inject internal constructor(objects: ObjectFactory) {
    /**
     * `--config` argument value.
     *
     * @see <a href="https://buf.build/docs/reference/cli/buf/">buf commands</a>
     */
    public val configFile: Property<File> = objects.property<File>()

    /**
     * `--log-format` option.
     *
     * @see <a href="https://buf.build/docs/reference/cli/buf/#log-format">buf --log-format</a>
     */
    public val logFormat: Property<LogFormat> = objects.property<LogFormat>().convention(LogFormat.Default)

    /**
     * Possible values for `--log-format` [logFormat] option.
     */
    public enum class LogFormat {
        Text,
        Color,
        Json,

        /**
         * Buf's default value.
         */
        Default,
        ;
    }

    /**
     * `--timeout` option.
     *
     * Value to Buf is passed in seconds using [Duration.inWholeSeconds].
     *
     * @see <a href="https://buf.build/docs/reference/cli/buf/#timeout">buf --timeout</a>
     */
    public val timeout: Property<Duration> = objects.property<Duration>().convention(Duration.ZERO)

    /**
     * `buf generate` options.
     *
     * @see <a href="https://buf.build/docs/reference/cli/buf/generate/">"buf generate" command</a>
     * @see [BUF_GEN_YAML]
     * @see [BufGenerateExtension]
     */
    public val generate: BufGenerateExtension = objects.newInstance(BufGenerateExtension::class.java)

    /**
     * Configures the `buf generate` options.
     *
     * @see <a href="https://buf.build/docs/reference/cli/buf/generate/">"buf generate" command</a>
     * @see [BUF_GEN_YAML]
     * @see [BufGenerateExtension]
     */
    public fun generate(configure: Action<BufGenerateExtension>) {
        configure.execute(generate)
    }

    /**
     * Use this extension to register custom Buf tasks
     * that will operate on the generated workspace.
     */
    public val tasks: BufTasksExtension = objects.newInstance(BufTasksExtension::class.java)

    /**
     * Use this extension to register custom Buf tasks
     * that will operate on the generated workspace.
     */
    public fun tasks(configure: Action<BufTasksExtension>) {
        configure.execute(tasks)
    }
}

/**
 * Allows registering custom Buf tasks that can operate on the generated workspace.
 */
public open class BufTasksExtension @Inject internal constructor(internal val project: Project) {
    /**
     * Returns a collection of all `buf` tasks registered in the project.
     */
    public fun all(): BufAllTasks {
        return BufAllTasksImpl(project, project.tasks.withType(BufExecTask::class.java))
    }

    /**
     * Registers a custom Buf task that operates on the generated workspace.
     *
     * Name conventions:
     * `lint` input for [name] will result in tasks
     * named 'bufLintMain' and 'bufLintTest' for Kotlin/JVM projects
     * and 'bufLintCommonMain', 'bufLintCommonTest', 'bufLintNativeMain', etc. for Kotlin/Multiplatform projects.
     */
    public fun <T : BufExecTask> registerWorkspaceTask(
        kClass: KClass<T>,
        name: String,
        configure: Action<T> = Action {},
    ): BufTasks<T> {
        @Suppress("UNCHECKED_CAST")
        customTasks.add(Definition(name, kClass, configure))

        return all().matchingType(kClass)
    }

    /**
     * Registers a custom Buf task that operates on the generated workspace.
     *
     * Name conventions:
     * `lint` input for [name] will result in tasks
     * named 'bufLintMain' and 'bufLintTest' for Kotlin/JVM projects
     * and 'bufLintCommonMain', 'bufLintCommonTest', 'bufLintNativeMain', etc. for Kotlin/Multiplatform projects.
     */
    public inline fun <reified T : BufExecTask> registerWorkspaceTask(
        name: String,
        configure: Action<T> = Action {},
    ): BufTasks<T> {
        return registerWorkspaceTask(T::class, name, configure)
    }

    internal val customTasks = project.objects.domainObjectContainer(Definition::class.java)

    internal class Definition<T : BufExecTask>(
        val name: String,
        val kClass: KClass<T>,
        val configure: Action<T>,
    )
}

/**
 * Options for the `buf generate` command.
 *
 * @see <a href="https://buf.build/docs/reference/cli/buf/generate/">"buf generate" command</a>
 * @see [BUF_GEN_YAML]
 */
public open class BufGenerateExtension @Inject constructor(internal val project: Project) {
    /**
     * Returns a collection of all `buf generate` tasks registered in the project.
     */
    public fun allTasks(): BufTasks<BufGenerateTask> {
        return BufTasksImpl(project, project.tasks.withType(BufGenerateTask::class.java), BufGenerateTask::class)
    }

    /**
     * `--include-imports` option.
     *
     * @see <a href="https://buf.build/docs/reference/cli/buf/generate/#include-imports">
     *     buf generate --include-imports
     * </a>
     * @see [ProtocPlugin.includeImports]
     */
    public val includeImports: Property<Boolean> = project.objects.property<Boolean>().convention(false)

    /**
     * `--include-wkt` option.
     *
     * @see <a href="https://buf.build/docs/reference/cli/buf/generate/#include-wkt">buf generate --include-wkt</a>
     * @see [ProtocPlugin.includeWkt]
     */
    public val includeWkt: Property<Boolean> = project.objects.property<Boolean>().convention(false)

    /**
     * `--error-format` option.
     *
     * @see <a href="https://buf.build/docs/reference/cli/buf/generate/#error-format">buf generate --error-format</a>
     */
    public val errorFormat: Property<ErrorFormat> = project.objects.property<ErrorFormat>()
        .convention(ErrorFormat.Default)

    /**
     * Possible values for `--error-format` option.
     *
     * @see <a href="https://buf.build/docs/reference/cli/buf/generate/#error-format">buf generate --error-format</a>
     */
    public enum class ErrorFormat(internal val cliValue: String) {
        Text("text"),
        Json("json"),
        Msvs("msvs"),
        Junit("junit"),
        GithubActions("github-actions"),

        /**
         * Buf's default value.
         */
        Default(""),
        ;
    }

    /**
     * Option to configure the indent sized for the generated code.
     *
     * Default value: `4`.
     */
    public val indentSize: Property<Int> = project.objects.property<Int>().convention(4)

    /**
     * Extension for configuring comments in the generated code.
     *
     * @see [BufCommentsExtension].
     */
    public val comments: BufCommentsExtension = project.objects.newInstance(BufCommentsExtension::class.java)

    /**
     * Extension for configuring comments in the generated code.
     *
     * @see [BufCommentsExtension].
     */
    public fun comments(configure: Action<BufCommentsExtension>) {
        configure.execute(comments)
    }
}

/**
 * Extension for configuring comments in the generated code.
 */
public open class BufCommentsExtension @Inject constructor(internal val project: Project) {
    /**
     * Whether to copy comments from the original source files.
     */
    public val copyComments: Property<Boolean> = project.objects.property<Boolean>().convention(true)

    /**
     * Whether to include file-level comments. This includes:
     * - Comments on the `package` declaration.
     * - Comments on the `syntax` declaration.
     * - Comments on the `editions` declaration.
     */
    public val includeFileLevelComments: Property<Boolean> = project.objects.property<Boolean>().convention(true)
}
