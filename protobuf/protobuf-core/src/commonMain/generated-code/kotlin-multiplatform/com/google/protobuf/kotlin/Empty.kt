@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*

/**
* A generic empty message that you can re-use to avoid defining duplicated
* empty messages in your APIs. A typical example is to use it as the request
* or the response type of an API method. For instance:
* 
*     service Foo {
*       rpc Bar(google.protobuf.Empty) returns (google.protobuf.Empty);
*     }
*/
@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.EmptyInternal.CODEC::class)
@kotlinx.rpc.protobuf.ProtoMessage
public interface Empty { 
    public companion object
}
