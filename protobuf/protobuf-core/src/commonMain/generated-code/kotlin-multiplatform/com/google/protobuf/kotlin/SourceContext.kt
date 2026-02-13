@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.grpc.codec.WithCodec
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
* `SourceContext` represents information about the source of a
* protobuf element, like the file in which it is defined.
*/
@WithCodec(SourceContextInternal.CODEC::class)
public interface SourceContext {
    /**
    * The path-qualified name of the .proto file that contained the associated
    * protobuf element.  For example: `"google/protobuf/source_context.proto"`.
    */
    public val fileName: String

    public companion object
}
