/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt.ClassNaming", "ClassName")

package kotlinx.rpc

import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin

class CompilerPluginCore : KotlinCompilerPluginSupportPlugin by compilerPlugin({
//    pluginSuffix = "-core"
})

//class CompilerPlugin1_9 : KotlinCompilerPluginSupportPlugin by compilerPlugin({
//    pluginSuffix = "-1_9"
//})
//
//class CompilerPlugin1_8 : KotlinCompilerPluginSupportPlugin by compilerPlugin({
//    pluginSuffix = "-1_8"
//})
//
//class CompilerPlugin1_7_2 : KotlinCompilerPluginSupportPlugin by compilerPlugin({
//    pluginSuffix = "-1_7_2"
//})
//
//class CompilerPlugin1_7 : KotlinCompilerPluginSupportPlugin by compilerPlugin({
//    pluginSuffix = "-1_7"
//})

//// Transitive dependencies do not work for Kotlin/Native
//// https://youtrack.jetbrains.com/issue/KT-53477/Native-Gradle-plugin-doesnt-add-compiler-plugin-transitive-dependencies-to-compiler-plugin-classpath
//fun compilerPluginForKotlin(kotlin: String): Class<out Plugin<*>> {
//    return when {
//        kotlin == "1.7.0" || kotlin == "1.7.10" -> CompilerPlugin1_7::class.java
//        kotlin.startsWith("1.7.2") -> CompilerPlugin1_7_2::class.java
//        kotlin.startsWith("1.8") -> CompilerPlugin1_8::class.java
//        kotlin.startsWith("1.9") -> CompilerPlugin1_9::class.java
//        else -> error("Unsupported kotlin version: $kotlin")
//    }
//}
