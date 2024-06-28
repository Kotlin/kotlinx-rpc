/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.test

import io.github.classgraph.ClassGraph
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.cli.common.CLICompiler
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.common.arguments.CommonCompilerArguments
import org.jetbrains.kotlin.cli.common.arguments.K2JSCompilerArguments
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.arguments.validateArguments
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.js.K2JSCompiler
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.*
import org.jetbrains.kotlin.util.ServiceLoaderLite
import java.io.File
import java.net.URI
import java.nio.file.Paths

internal fun File.hasKotlinFileExtension() = hasFileExtension(listOf("kt", "kts"))

internal fun File.hasFileExtension(extensions: List<String>) =
    extensions.any { it.equals(extension, ignoreCase = true) }

abstract class KSource {
    internal abstract fun writeIfNeeded(dir: File): File

    companion object {

        /**
         * Create a new Kotlin source file for the compilation when the compilation is run
         */
        fun kotlin(name: String, @Language("kotlin") contents: String, trimIndent: Boolean = true): KSource {
            require(File(name).hasKotlinFileExtension())
            val finalContents = if (trimIndent) contents.trimIndent() else contents
            return new(name, finalContents)
        }

        /**
         * Create a new source file for the compilation when the compilation is run
         */
        fun new(name: String, contents: String) = object : KSource() {
            override fun writeIfNeeded(dir: File): File {
                val file = dir.resolve(name)
                file.parentFile.mkdirs()
                file.createNewFile()

                file.bufferedWriter().use {
                    it.write(contents)
                    it.flush()
                }

                return file
            }
        }

        /**
         * Compile an existing source file
         */
        fun fromPath(path: File) = object : KSource() {
            init {
                require(path.isFile)
            }

            override fun writeIfNeeded(dir: File): File = path
        }
    }
}

internal object HostEnvironment {
    val hostClasspath by lazy {
        getHostClasspaths()
    }

    val kotlinStdLibJar: File? by lazy {
        findInClasspath(kotlinDependencyRegex("(kotlin-stdlib|kotlin-runtime)"))
    }

    val kotlinStdLibCommonJar: File? by lazy {
        findInClasspath(kotlinDependencyRegex("kotlin-stdlib-common"))
    }

    val kotlinStdLibJsJar: File? by lazy {
        findInClasspath(kotlinDependencyRegex("kotlin-stdlib-js"))
    }

    val kotlinStdLibJdkJar: File? by lazy {
        findInClasspath(kotlinDependencyRegex("kotlin-stdlib-jdk[0-9]+"))
    }

    val kotlinReflectJar: File? by lazy {
        findInClasspath(kotlinDependencyRegex("kotlin-reflect"))
    }

    val kotlinScriptRuntimeJar: File? by lazy {
        findInClasspath(kotlinDependencyRegex("kotlin-script-runtime"))
    }

    val toolsJar: File? by lazy {
        findInClasspath(Regex("tools.jar"))
    }

    private fun kotlinDependencyRegex(prefix: String): Regex {
        return Regex("$prefix(-[0-9]+\\.[0-9]+(\\.[0-9]+)?)([-0-9a-zA-Z]+)?\\.jar")
    }

    /** Tries to find a file matching the given [regex] in the host process' classpath */
    private fun findInClasspath(regex: Regex): File? {
        val jarFile = hostClasspath.firstOrNull { classpath ->
            classpath.name.matches(regex)
            //TODO("check that jar file actually contains the right classes")
        }
        return jarFile
    }

    /** Returns the files on the classloader's classpath and modulepath */
    private fun getHostClasspaths(): List<File> {
        val classGraph = ClassGraph()
            .enableSystemJarsAndModules()
            .removeTemporaryFilesAfterScan()

        val classpaths = classGraph.classpathFiles
        val modules = classGraph.modules.mapNotNull { it.locationFile }

        return (classpaths + modules).distinctBy(File::getAbsolutePath)
    }
}

@OptIn(ExperimentalCompilerApi::class)
@Suppress("DEPRECATION")
internal class MainComponentAndPluginRegistrar : ComponentRegistrar, CompilerPluginRegistrar() {

    override val supportsK2: Boolean
        get() = safeGetThreadLocalParameters(logCallerName = "supportsK2")?.supportsK2 ?: false

    // Handle unset parameters gracefully because this plugin may be accidentally called by other tools that
    // discover it on the classpath (for example the kotlin jupyter kernel).
    private fun safeGetThreadLocalParameters(logCallerName: String): ThreadLocalParameters? {
        val params = threadLocalParameters.get()
        if (params == null) {
            System.err.println(
                "WARNING: ${MainComponentAndPluginRegistrar::class.simpleName}::$logCallerName " +
                        "accessed before thread local parameters have been set."
            )
        }

        return params
    }

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val parameters = safeGetThreadLocalParameters(logCallerName = "registerExtensions") ?: return

        parameters.compilerPluginRegistrars.forEach { pluginRegistrar ->
            with(pluginRegistrar) {
                registerExtensions(configuration)
            }
        }
    }

    // Legacy plugins
    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        val parameters = safeGetThreadLocalParameters(logCallerName = "registerProjectComponents") ?: return

        /*
         * The order of registering plugins here matters. If the kapt plugin is registered first, then
         * it will be executed first and any changes made to the AST by later plugins won't apply to the
         * generated stub files and thus won't be visible to any annotation processors. So we decided
         * to register third-party plugins before kapt and hope that it works, although we don't
         * know for sure if that is the correct way.
         */
        parameters.componentRegistrars.forEach { componentRegistrar ->
            componentRegistrar.registerProjectComponents(project, configuration)
        }
    }


    companion object {
        /** This compiler plugin is instantiated by K2JVMCompiler using
         *  a service locator. So we can't just pass parameters to it easily.
         *  Instead, we need to use a thread-local global variable to pass
         *  any parameters that change between compilations
         */
        val threadLocalParameters: ThreadLocal<ThreadLocalParameters> = ThreadLocal()
    }

    data class ThreadLocalParameters(
        @Deprecated("Remove once ComponentRegistrar is deprecated with error")
        val componentRegistrars: List<ComponentRegistrar>,
        val compilerPluginRegistrars: List<CompilerPluginRegistrar>,
        val supportsK2: Boolean
    )
}

@OptIn(ExperimentalCompilerApi::class)
class KCompilation {
    var workingDir = File(".").resolve("build/kcp-test")
    var sourcesDir: File = workingDir.resolve("sources")
    var classesDir: File = workingDir.resolve("classes")
    var jsOutputDir: File = workingDir.resolve("jsOutput")
    var sources: List<KSource> = emptyList()
    var inheritClassPath: Boolean = false
    var compilerPluginRegistrars: List<CompilerPluginRegistrar> = emptyList()
    var useK2: Boolean = false
    var languageVersion: String? = null

    fun compileJvm(): ExitCode {
        val sourceFiles = sources.map { it.writeIfNeeded(sourcesDir) }

        // step 3: compile Kotlin files
        return compileJvmKotlin(sourceFiles)
    }

    fun compileJs(): ExitCode {
        val sourceFiles = sources.map { it.writeIfNeeded(sourcesDir) }

        return withSystemProperty("idea.use.native.fs.for.win", "false") {
            compileJsKotlin(sourceFiles)
        }
    }

    private fun compileJvmKotlin(sourceFiles: List<File>): ExitCode {
        return compileKotlin(sourceFiles, K2JVMCompiler(), commonK2JVMArgs())
    }

    private fun compileJsKotlin(sourceFiles: List<File>): ExitCode {
        return compileKotlin(sourceFiles, K2JSCompiler(), commonK2JSArgs())
    }

    // setup common arguments for the two kotlinc calls
    private fun commonK2JSArgs() = commonArguments(K2JSCompilerArguments()) { args ->
        args.moduleKind = "commonjs"
        args.moduleName = "commonjs"
        args.outputDir = jsOutputDir.absolutePath
        args.outputFile = File(jsOutputDir, "test.js").absolutePath

        args.sourceMapBaseDirs = jsClasspath().joinToString(separator = File.pathSeparator)
        args.libraries = listOfNotNull(HostEnvironment.kotlinStdLibJsJar).joinToString(separator = ":")

        args.irProduceKlibDir = false
        args.irProduceKlibFile = false
        args.irProduceJs = true
        args.irDce = false
        args.irDcePrintReachabilityInfo = false
        args.irOnly = true
        args.irModuleName = null
        args.generateDts = false

        args.noStdlib = true
    }

    // setup common arguments for the two kotlinc calls
    private fun commonK2JVMArgs() = commonArguments(K2JVMCompilerArguments()) { args ->
        args.destination = classesDir.absolutePath
        args.classpath = commonClasspaths().joinToString(separator = File.pathSeparator)

        args.noJdk = true

        args.includeRuntime = false

        // the compiler should never look for stdlib or reflect in the
        // kotlinHome directory (which is null anyway). We will put them
        // in the classpath manually if they're needed
        args.noStdlib = true
        args.noReflect = true

        args.jvmTarget = JvmTarget.DEFAULT.description
        args.javaParameters = false
        args.useOldBackend = false

        args.noCallAssertions = false
        args.noParamAssertions = false
        args.noReceiverAssertions = false

        args.noOptimize = false

        args.assertionsMode = JVMAssertionsMode.DEFAULT.description

        args.inheritMultifileParts = false
        args.useTypeTable = false


        args.jvmDefault = JvmDefaultMode.DEFAULT.description
        args.strictMetadataVersionSemantics = false
        args.sanitizeParentheses = false

        args.javaPackagePrefix = null
        args.suppressMissingBuiltinsError = false
    }

    private fun <CompilerArgs : CommonCompilerArguments> commonArguments(
        args: CompilerArgs,
        configuration: (args: CompilerArgs) -> Unit
    ): CompilerArgs {
        args.verbose = true

        args.suppressWarnings = true
        args.allWarningsAsErrors = false
        args.reportOutputFiles = true
        args.reportPerf = false
        args.multiPlatform = false
        args.noCheckActual = false

        args.useK2 = useK2
        args.languageVersion = languageVersion

        configuration(args)

        validateArguments(args.errors)?.let {
            throw IllegalArgumentException("Errors parsing kotlinc CLI arguments:\n$it")
        }

        return args
    }

    private fun <CompilerArgs : CommonCompilerArguments> compileKotlin(
        sources: List<File>,
        compiler: CLICompiler<CompilerArgs>,
        arguments: CompilerArgs,
    ): ExitCode {
        /**
         * Here the list of compiler plugins is set
         *
         * To avoid that the annotation processors are executed twice,
         * the list is set to empty
         */
        MainComponentAndPluginRegistrar.threadLocalParameters.set(
            MainComponentAndPluginRegistrar.ThreadLocalParameters(
                componentRegistrars = emptyList(),
                compilerPluginRegistrars = compilerPluginRegistrars,
                supportsK2 = false,
            )
        )

        // in this step also include source files generated by kapt in the previous step
        val args = arguments.also { args ->
            args.freeArgs =
                sources.map(File::getAbsolutePath).distinct() + if (sources.none(File::hasKotlinFileExtension)) {
                    /* __HACK__: The Kotlin compiler expects at least one Kotlin source file or it will crash,
                       so we trick the compiler by just including an empty .kt-File. We need the compiler to run
                       even if there are no Kotlin files because some compiler plugins may also process Java files. */
                    listOf(KSource.new("emptyKotlinFile.kt", "").writeIfNeeded(sourcesDir).absolutePath)
                } else {
                    emptyList()
                }

            args.pluginClasspaths = (args.pluginClasspaths ?: emptyArray()) +
                    /** The resources path contains the MainComponentRegistrar and MainCommandLineProcessor which will
                    be found by the Kotlin compiler's service loader. We add it only when the user has actually given
                    us ComponentRegistrar instances to be loaded by the MainComponentRegistrar because the experimental
                    K2 compiler doesn't support plugins yet. This way, users of K2 can prevent MainComponentRegistrar
                    from being loaded and crashing K2 by setting both [componentRegistrars] and [commandLineProcessors]
                    and [compilerPluginRegistrars] to the emptyList. */
                    if (compilerPluginRegistrars.isNotEmpty()) {
                        arrayOf(getResourcesPath())
                    } else {
                        emptyArray()
                    }
        }

        val compilerMessageCollector = PrintingMessageCollector(System.out, MessageRenderer.GRADLE_STYLE, true)

        return convertKotlinExitCode(
            compiler.exec(compilerMessageCollector, Services.EMPTY, args)
        )
    }

    private fun getResourcesPath(): String {
        val resourceName = "META-INF/services/org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar"
        return this::class.java.classLoader.getResources(resourceName)
            .asSequence()
            .mapNotNull { url ->
                val uri = URI.create(url.toString().removeSuffix("/$resourceName"))
                when (uri.scheme) {
                    "jar" -> Paths.get(URI.create(uri.schemeSpecificPart.removeSuffix("!")))
                    "file" -> Paths.get(uri)
                    else -> return@mapNotNull null
                }.toAbsolutePath()
            }
            .find { resourcesPath ->
                ServiceLoaderLite.findImplementations(
                    CompilerPluginRegistrar::class.java,
                    listOf(resourcesPath.toFile())
                ).any { implementation ->
                    implementation == MainComponentAndPluginRegistrar::class.java.name
                }
            }?.toString() ?: throw AssertionError("Could not get path to CompilerPluginRegistrar service from META-INF")
    }

    private fun commonClasspaths() = buildList {
        with(HostEnvironment) {
            addAll(
                listOfNotNull(
                    kotlinStdLibJar, kotlinStdLibCommonJar, kotlinStdLibJdkJar,
                    kotlinReflectJar, kotlinScriptRuntimeJar
                )
            )

            if (inheritClassPath) {
                addAll(hostClasspath)
            }
        }
    }.distinct()

    private fun jsClasspath() = buildList {
        with(HostEnvironment) {
            addAll(listOfNotNull(kotlinStdLibCommonJar, kotlinStdLibJsJar))

            if (inheritClassPath) {
                addAll(hostClasspath)
            }
        }
    }.distinct()

    enum class ExitCode {
        OK, INTERNAL_ERROR, COMPILATION_ERROR, SCRIPT_EXECUTION_ERROR
    }

}

internal fun convertKotlinExitCode(code: ExitCode) = when (code) {
    ExitCode.OK -> KCompilation.ExitCode.OK
    ExitCode.INTERNAL_ERROR -> KCompilation.ExitCode.INTERNAL_ERROR
    ExitCode.COMPILATION_ERROR -> KCompilation.ExitCode.COMPILATION_ERROR
    ExitCode.SCRIPT_EXECUTION_ERROR -> KCompilation.ExitCode.SCRIPT_EXECUTION_ERROR
    ExitCode.OOM_ERROR -> throw OutOfMemoryError("Kotlin compiler ran out of memory")
}

internal fun <T> withSystemProperty(key: String, value: String, f: () -> T): T =
    withSystemProperties(mapOf(key to value), f)

internal fun <T> withSystemProperties(properties: Map<String, String>, f: () -> T): T {
    val previousProperties = mutableMapOf<String, String?>()

    for ((key, value) in properties) {
        previousProperties[key] = System.getProperty(key)
        System.setProperty(key, value)
    }

    try {
        return f()
    } finally {
        for ((key, value) in previousProperties) {
            if (value != null) {
                System.setProperty(key, value)
            }
        }
    }
}
