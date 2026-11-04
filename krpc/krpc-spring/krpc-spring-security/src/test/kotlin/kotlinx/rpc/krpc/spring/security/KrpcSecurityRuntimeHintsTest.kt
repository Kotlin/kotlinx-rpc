/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.security

import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates
import org.springframework.core.io.support.SpringFactoriesLoader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KrpcSecurityRuntimeHintsTest {
    @Test
    fun registersReflectionHintsForSecurityTypes() {
        val hints = RuntimeHints()
        KrpcSecurityRuntimeHints().registerHints(hints, javaClass.classLoader)

        val reflection = RuntimeHintsPredicates.reflection()
        assertTrue(
            reflection.onType(SecurityContextEnricher::class.java)
                .withMemberCategories(
                    MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                    MemberCategory.INVOKE_PUBLIC_METHODS,
                )
                .test(hints),
        )
        assertTrue(
            reflection.onType(SecurityContextElement::class.java)
                .withMemberCategories(
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                    MemberCategory.ACCESS_DECLARED_FIELDS,
                )
                .test(hints),
        )
        assertTrue(
            reflection.onType(SecurityContextElement.Key::class.java)
                .withMemberCategories(
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                    MemberCategory.ACCESS_DECLARED_FIELDS,
                )
                .test(hints),
        )
    }

    @Test
    fun aotFactoriesListTheRegistrar() {
        val registrars = SpringFactoriesLoader.forResourceLocation("META-INF/spring/aot.factories")
            .load(RuntimeHintsRegistrar::class.java)

        assertEquals(1, registrars.count { it is KrpcSecurityRuntimeHints })
    }
}
