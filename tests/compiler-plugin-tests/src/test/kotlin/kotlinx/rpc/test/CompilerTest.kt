/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.test

import kotlinx.rpc.codegen.RPCCompilerPlugin
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlinx.serialization.compiler.extensions.SerializationComponentRegistrar
import kotlin.test.Test
import kotlin.test.fail

class CompilerTest {
    @OptIn(ExperimentalCompilerApi::class)
    @Test
    fun simpleCompilationTest() {
        val source = KSource.kotlin("service.kt", """
            import kotlinx.rpc.*
            import kotlinx.rpc.internal.RPCMethodClassArguments
            import kotlinx.rpc.internal.InternalRPCApi
            import kotlinx.coroutines.flow.*
            import kotlinx.serialization.Serializable
            import kotlinx.serialization.Contextual
            
            @OptIn(InternalRPCApi::class)
            interface MainService : RPC {
                @RPCEagerField
                val flow: Flow<Int>

                val stateFlow: StateFlow<Int>

                val sharedFlow: SharedFlow<Int>

                suspend fun empty()

                suspend fun hello(arg1: String, arg2: Flow<Int>): Int

//                class `${'$'}rpcServiceStub` {
//                    @Serializable
//                    internal object `empty${'$'}rpcMethod` : RPCMethodClassArguments {
//                        override fun asArray() = emptyArray<Any?>()
//                    }
//
//                    @Serializable
//                    internal class `hello${'$'}rpcMethod`(
//                        val arg1: String,
//                        val arg2: Flow<Int>,
//                    ): RPCMethodClassArguments {
//                        override fun asArray() = arrayOf<Any?>(arg1, arg2)
//                    }
//
//                    companion object
//                }
            }
        """.trimIndent())

        val compilation = KCompilation().apply {
            sources = listOf(source)
            inheritClassPath = true
            languageVersion = "2.0"

            compilerPluginRegistrars = listOf(RPCCompilerPlugin(), SerializationComponentRegistrar())
        }

        // js does not work for now
//        val resultJs = compilation.compileJs()
        val resultJvm = compilation.compileJvm()

        if (resultJvm != KCompilation.ExitCode.OK) {
            fail("Failed: jvm: $resultJvm")
        }
    }
}
