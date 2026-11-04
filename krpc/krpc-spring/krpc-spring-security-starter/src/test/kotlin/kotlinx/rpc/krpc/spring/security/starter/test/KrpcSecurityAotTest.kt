/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.security.starter.test

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.internal.WithServiceDescriptor
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.krpc.spring.security.SecurityContextEnricher
import kotlinx.rpc.withService
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.TypeReference
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
import java.util.function.Predicate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KrpcSecurityAotTest {
    @Test
    fun `registers runtime hints for the security integration and the secured service`() {
        val generationContext = TestGenerationContext(SecurityStarterTestApplication::class.java)
        processAheadOfTime(generationContext)

        val hints = generationContext.runtimeHints
        val reflection = RuntimeHintsPredicates.reflection()
        assertHint(
            hints,
            reflection.onType(SecurityContextEnricher::class.java).withMemberCategories(
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                MemberCategory.INVOKE_PUBLIC_METHODS,
            ),
        )
        // SecurityContextElement is internal to krpc-spring-security, so the test uses its binary names.
        assertHint(
            hints,
            reflection.onType(TypeReference.of(SECURITY_CONTEXT_ELEMENT)).withMemberCategories(
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.ACCESS_DECLARED_FIELDS,
            ),
        )
        assertHint(hints, reflection.onType(TypeReference.of("$SECURITY_CONTEXT_ELEMENT\$Key")))

        assertHint(hints, reflection.onType(Rpc::class.java))
        assertHint(hints, reflection.onType(SecuredService::class.java))
        assertHint(hints, reflection.onMethodInvocation(SecuredService::class.java, "currentUser"))
        assertHint(hints, reflection.onMethodInvocation(SecuredService::class.java, "adminOnly"))

        @OptIn(InternalRpcApi::class)
        val stubClass = SecuredService::class.java.getAnnotation(WithServiceDescriptor::class.java).stub.java
        assertHint(hints, reflection.onType(stubClass))
    }

    // The generated code accesses package-private Spring Boot classes.
    @Test
    @CompileWithForkedClassLoader
    fun `an AOT-processed secured application serves authenticated calls`() {
        disableTomcatUrlStreamHandlerFactory()
        val generationContext = TestGenerationContext(SecurityStarterTestApplication::class.java)
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

            val httpClient = HttpClient(CIO) { install(WebSockets) }
            try {
                assertEquals(
                    setOf("securityContextEnricher"),
                    context.getBeansOfType(SecurityContextEnricher::class.java).keys,
                )
                val port = checkNotNull(context.webServer) { "The AOT context must start a web server." }.port
                runBlocking {
                    assertServesAuthenticatedCalls(httpClient, port)
                }
            } finally {
                try {
                    httpClient.close()
                } finally {
                    context.close()
                }
            }
        }
    }

    private suspend fun assertServesAuthenticatedCalls(httpClient: HttpClient, port: Int) {
        val userClient = httpClient.securedKrpcClient(port, "user", "user-password")
        try {
            val service = userClient.withService<SecuredService>()

            assertEquals("user", withRpcTimeout { service.currentUser() })
            assertEquals("user", withRpcTimeout { service.currentUserAfterDispatcherSwitch() })

            val denied = assertFailsWith<Throwable> { withRpcTimeout { service.adminOnly() } }
            assertFalse(denied.isCancellation(), "Expected an access denial, got a timeout: $denied")
            assertTrue(denied.isAccessDenied(), "Expected an access denial, got: $denied")
        } finally {
            userClient.close()
        }

        val adminClient = httpClient.securedKrpcClient(port, "admin", "admin-password")
        try {
            assertEquals("admin", withRpcTimeout { adminClient.withService<SecuredService>().adminOnly() })
        } finally {
            adminClient.close()
        }

        val anonymousClient = httpClient.securedKrpcClient(port)
        try {
            val rejected = assertFailsWith<Throwable> {
                withRpcTimeout { anonymousClient.withService<SecuredService>().currentUser() }
            }
            assertFalse(rejected.isCancellation(), "Expected a rejected handshake, got a timeout: $rejected")
            assertTrue(rejected.isUnauthorizedHandshakeFailure(), "Expected a 401 handshake, got: $rejected")
        } finally {
            anonymousClient.close()
        }
    }

    private fun processAheadOfTime(generationContext: TestGenerationContext): ClassName {
        val context = AnnotationConfigServletWebServerApplicationContext()
        context.environment.propertySources.addFirst(testProperties())
        context.register(SecurityStarterTestApplication::class.java)
        return ApplicationContextAotGenerator().processAheadOfTime(context, generationContext)
    }

    private fun testProperties() = MapPropertySource(
        "krpcSecurityAotTest",
        mapOf("server.port" to "0", "logging.level.root" to "ERROR"),
    )

    private fun assertHint(hints: RuntimeHints, predicate: Predicate<RuntimeHints>) {
        assertTrue(predicate.test(hints), "Missing runtime hint: $predicate")
    }

    private companion object {
        const val SECURITY_CONTEXT_ELEMENT = "kotlinx.rpc.krpc.spring.security.SecurityContextElement"
    }
}
