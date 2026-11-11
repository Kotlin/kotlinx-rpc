/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import com.autonomousapps.kit.GradleBuilder.buildAndFail
import com.autonomousapps.kit.Source
import com.autonomousapps.kit.gradle.Plugin
import com.autonomousapps.kit.gradle.Repositories
import com.autonomousapps.kit.gradle.Repository
import org.jetbrains.kotlin.compiler.plugin.devkit.test.AbstractDevKitGradleProject
import org.jetbrains.kotlin.compiler.plugin.devkit.test.assertOutputContains
import org.jetbrains.kotlin.compiler.plugin.devkit.test.pluginUnderTestVersion
import org.junit.Test

/**
 * Checks that the Kotlin compiler of each supported version loads the compiler plugin from the Gradle plugin.
 *
 * The test project doesn't depend on the kotlinx.rpc libraries, because this build can't publish them.
 * It declares its own `@Rpc` annotation with the same name, and expects a diagnostic of the compiler plugin.
 */
public class CompilerPluginFunctionalTest {
    @Test
    public fun compilerPluginReportsDiagnostic() {
        val project = object : AbstractDevKitGradleProject() {
            override val pluginUnderTest: Plugin
                get() = Plugin("org.jetbrains.kotlinx.rpc.plugin", pluginUnderTestVersion)

            // The published Gradle plugin depends on the API watchdog annotations. Only kt-lib/eap has them.
            override fun repositories(defaults: List<Repository>): Repositories {
                return super.repositories(
                    defaults + Repository.ofMaven("https://packages.jetbrains.team/maven/p/kt-lib/eap")
                )
            }

            override fun sources(): List<Source> = listOf(
                source(
                    source = "annotation class Rpc",
                    fileNameWithoutExtension = "Rpc",
                    packageName = "kotlinx.rpc.annotations",
                ),
                source(
                    source = """
                        import kotlinx.rpc.annotations.Rpc

                        @Rpc
                        class NotAnInterface
                    """.trimIndent(),
                    fileNameWithoutExtension = "NotAnInterface",
                ),
            )
        }.gradleProject

        val result = buildAndFail(project.rootDir, "compileKotlin")

        result.assertOutputContains("annotation is only applicable to interfaces and annotation classes")
    }
}
