/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal.dummy

/**
 * A dummy object that allows us to create library metadata for the protobuf-core aggregate module.
 * Without this source, the Gradle task will fail.
 */
@Suppress("unused")
internal object ProtobufCoreMarker
