@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.grpc.codec.WithCodec
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.GeneratedProtoMessage

/**
* A generic empty message that you can re-use to avoid defining duplicated
* empty messages in your APIs. A typical example is to use it as the request
* or the response type of an API method. For instance:
* 
*     service Foo {
*       rpc Bar(google.protobuf.Empty) returns (google.protobuf.Empty);
*     }
*/
@GeneratedProtoMessage
@WithCodec(EmptyInternal.CODEC::class)
public interface Empty {
    public companion object
}
