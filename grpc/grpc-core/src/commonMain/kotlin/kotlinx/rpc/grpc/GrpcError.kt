/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

public sealed class GrpcError : RuntimeException {
    protected constructor(message: String) : super(message)
    protected constructor(message: String, cause: Throwable) : super(message, cause)
}


public class InvalidProtobufError(message: String) : GrpcError(message) {
    public companion object {
        internal fun missingRequiredField(messageName: String, fieldName: String) =
            InvalidProtobufError("Message '$messageName' is missing a required field: $fieldName")
    }
}

