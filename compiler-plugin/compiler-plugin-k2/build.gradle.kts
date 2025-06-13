/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import util.enableContextParameters
import util.otherwise
import util.whenForIde
import util.whenKotlinCompilerIsAtLeast

plugins {
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.compiler.specific.module)
    alias(libs.plugins.shadow.jar)
}

/*
This is a hack to solve the next problem:

There is PsiElement class that is used in compiler and plugin.

If we use kotlin-compiler-embeddable.jar to compile the module,
PsiElement is org.jetbrains.kotlin.com.jetbrains.intellij.PsiElement.
And it works ok in the user projects!

But.

If we run tests, which use kotlin-compiler.jar, we run into ClassNotFoundException.
Because the class it has in th classpath is com.jetbrains.intellij.PsiElement

- Alright, we can use kotlin-compiler.jar to compile the plugin.
- No, because the same error now will occur in the user projects,
  but it is com.jetbrains.intellij.PsiElement that is not found.

- Ok, we can use kotlin-compiler-embeddable.jar to compile tests.
- Then we ran into java.lang.VerifyError: Bad type on operand stack, which I have no idea how to fix.

This solution replaces org.jetbrains.kotlin.com.jetbrains.intellij.PsiElement usages in plugin
with com.jetbrains.intellij.PsiElement only for the tests, fixing both use cases.
It is basically a reverse engineering of what Kotlin does for the embedded jar.
 */
val shadowJar = tasks.named<ShadowJar>("shadowJar") {
    configurations = listOf(project.configurations.compileClasspath.get())
    relocate("org.jetbrains.kotlin.com.intellij.psi", "com.intellij.psi")

    exclude("javaslang/**")
    exclude("kotlin/**")
    exclude("messages/**")
    exclude("misc/**")
    exclude("org/**")

    archiveFileName.set("plugin-k2-for-tests.jar")

    /**
     * Same problem as above, but in IDE
     */
    whenForIde {
        archiveClassifier.set("for-ide")
    } otherwise {
        archiveClassifier.set("tests")
    }
}

tasks.jar {
    finalizedBy(shadowJar)
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled

    rootProject.whenKotlinCompilerIsAtLeast(2, 2, 0) {
        enableContextParameters()
    }
}

dependencies {
    compileOnly(libs.kotlin.compiler)

    implementation(projects.compilerPluginCommon)
}
