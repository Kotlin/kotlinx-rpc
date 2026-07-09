/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// RUN_PIPELINE_TILL: FRONTEND

import kotlinx.rpc.annotations.Rpc

@Rpc
interface WithinLimit {
    suspend fun atLimit(
        required: Int,
        p1: Int = 1,
        p2: Int = 2,
        p3: Int = 3,
        p4: Int = 4,
        p5: Int = 5,
        p6: Int = 6,
        p7: Int = 7,
        p8: Int = 8,
    ): Int
}

@Rpc
interface OverLimit {
    <!TOO_MANY_OPTIONAL_PARAMETERS_IN_RPC_FUNCTION!>suspend fun tooMany(
        required: Int,
        p1: Int = 1,
        p2: Int = 2,
        p3: Int = 3,
        p4: Int = 4,
        p5: Int = 5,
        p6: Int = 6,
        p7: Int = 7,
        p8: Int = 8,
        p9: Int = 9,
    ): Int<!>
}

/* GENERATED_FIR_TAGS: functionDeclaration, integerLiteral, interfaceDeclaration, suspend */
