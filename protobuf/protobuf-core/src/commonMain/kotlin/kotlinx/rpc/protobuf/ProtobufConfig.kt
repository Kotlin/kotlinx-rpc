/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf

import kotlinx.rpc.grpc.codec.CodecConfig


public class ProtobufConfig(
    public val discardUnknownFields: Boolean = false
): CodecConfig