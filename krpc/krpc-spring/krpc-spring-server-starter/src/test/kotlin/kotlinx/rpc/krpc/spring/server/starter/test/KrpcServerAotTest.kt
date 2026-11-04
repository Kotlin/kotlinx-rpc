/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server.starter.test

import kotlinx.coroutines.runBlocking
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.internal.WithServiceDescriptor
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.withService
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates
import org.springframework.aot.test.generate.TestGenerationContext
import org.springframework.boot.web.server.servlet.context.AnnotationConfigServletWebServerApplicationContext
import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.aot.ApplicationContextAotGenerator
import org.springframework.context.support.GenericApplicationContext
import org.springframework.core.env.MapPropertySource
import org.springframework.core.test.tools.CompileWithForkedClassLoader
import org.springframework.core.test.tools.TestCompiler
import org.springframework.javapoet.ClassName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KrpcServerAotTest {
    @Test
    fun `registers runtime hints for kRPC services`() {
        val generationContext = TestGenerationContext(StarterTestApplication::class.java)
        processAheadOfTime(generationContext)

        val hints = generationContext.runtimeHints
        val reflection = RuntimeHintsPredicates.reflection()
        assertHint(hints, reflection.onType(Rpc::class.java))
        assertHint(hints, reflection.onType(TestService::class.java))
        assertHint(hints, reflection.onType(EchoService::class.java))
        assertHint(hints, reflection.onMethodInvocation(EchoService::class.java, "echo"))
        assertHint(hints, reflection.onFieldAccess(Greeting::class.java, "text"))

        @OptIn(InternalRpcApi::class)
        val stubClass = EchoService::class.java.getAnnotation(WithServiceDescriptor::class.java).stub.java
        assertHint(hints, reflection.onType(stubClass))
        // The stub is a companion object, so its instance is a static field of the enclosing class.
        assertHint(hints, reflection.onFieldAccess(stubClass.enclosingClass, stubClass.simpleName))
    }

    // The generated code accesses package-private Spring Boot classes.
    @Test
    @CompileWithForkedClassLoader
    fun `an AOT-processed application serves RPC calls`() {
        disableTomcatUrlStreamHandlerFactory()
        val generationContext = TestGenerationContext(StarterTestApplication::class.java)
        val initializerClassName = processAheadOfTime(generationContext)
        generationContext.writeGeneratedContent()

        TestCompiler.forSystem().with(generationContext).compile { compiled ->
            @Suppress("UNCHECKED_CAST")
            val initializer = compiled.getInstance(
                ApplicationContextInitializer::class.java,
                initializerClassName.canonicalName(),
            ) as ApplicationContextInitializer<GenericApplicationContext>

            val context = ServletWebServerApplicationContext()
            context.environment.propertySources.addFirst(testProperties())
            initializer.initialize(context)
            context.refresh()

            val httpClient = webSocketHttpClient()
            try {
                val port = checkNotNull(context.webServer) { "The AOT context must start a web server." }.port
                runBlocking {
                    val client = httpClient.krpcClient(port)
                    val service = client.withService<EchoService>()

                    assertEquals("aot", withRpcTimeout { service.echo("aot") })
                    assertEquals(Greeting("Hello, native!"), withRpcTimeout { service.greet(Greeting("native")) })

                    client.close()
                }
            } finally {
                httpClient.close()
                context.close()
            }
        }
    }

    private fun processAheadOfTime(generationContext: TestGenerationContext): ClassName {
        val context = AnnotationConfigServletWebServerApplicationContext()
        context.environment.propertySources.addFirst(testProperties())
        context.register(StarterTestApplication::class.java)
        return ApplicationContextAotGenerator().processAheadOfTime(context, generationContext)
    }

    private fun testProperties() = MapPropertySource(
        "krpcAotTest",
        mapOf("server.port" to "0", "logging.level.root" to "ERROR"),
    )

    private fun assertHint(hints: RuntimeHints, predicate: java.util.function.Predicate<RuntimeHints>) {
        assertTrue(predicate.test(hints), "Missing runtime hint: $predicate")
    }
}
