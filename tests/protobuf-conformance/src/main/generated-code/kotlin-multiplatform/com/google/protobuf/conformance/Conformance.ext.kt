@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.conformance

import kotlin.jvm.JvmInline
import kotlinx.rpc.internal.utils.*

/**
* Constructs a new message.
* ```
* val message = TestStatus {
*    name = ...
* }
* ```
*/
operator fun com.google.protobuf.conformance.TestStatus.Companion.invoke(body: com.google.protobuf.conformance.TestStatusInternal.() -> Unit): com.google.protobuf.conformance.TestStatus { 
    val msg = com.google.protobuf.conformance.TestStatusInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    name = ...
* }
* ```
*/
fun com.google.protobuf.conformance.TestStatus.copy(body: com.google.protobuf.conformance.TestStatusInternal.() -> Unit = {}): com.google.protobuf.conformance.TestStatus { 
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = FailureSet {
*    test = ...
* }
* ```
*/
operator fun com.google.protobuf.conformance.FailureSet.Companion.invoke(body: com.google.protobuf.conformance.FailureSetInternal.() -> Unit): com.google.protobuf.conformance.FailureSet { 
    val msg = com.google.protobuf.conformance.FailureSetInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    test = ...
* }
* ```
*/
fun com.google.protobuf.conformance.FailureSet.copy(body: com.google.protobuf.conformance.FailureSetInternal.() -> Unit = {}): com.google.protobuf.conformance.FailureSet { 
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = ConformanceRequest {
*    requestedOutputFormat = ...
* }
* ```
*/
operator fun com.google.protobuf.conformance.ConformanceRequest.Companion.invoke(body: com.google.protobuf.conformance.ConformanceRequestInternal.() -> Unit): com.google.protobuf.conformance.ConformanceRequest { 
    val msg = com.google.protobuf.conformance.ConformanceRequestInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    requestedOutputFormat = ...
* }
* ```
*/
fun com.google.protobuf.conformance.ConformanceRequest.copy(body: com.google.protobuf.conformance.ConformanceRequestInternal.() -> Unit = {}): com.google.protobuf.conformance.ConformanceRequest { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.conformance.ConformanceRequest] instance.
*/
val com.google.protobuf.conformance.ConformanceRequest.presence: com.google.protobuf.conformance.ConformanceRequestPresence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = ConformanceResponse {
*    result = ...
* }
* ```
*/
operator fun com.google.protobuf.conformance.ConformanceResponse.Companion.invoke(body: com.google.protobuf.conformance.ConformanceResponseInternal.() -> Unit): com.google.protobuf.conformance.ConformanceResponse { 
    val msg = com.google.protobuf.conformance.ConformanceResponseInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    result = ...
* }
* ```
*/
fun com.google.protobuf.conformance.ConformanceResponse.copy(body: com.google.protobuf.conformance.ConformanceResponseInternal.() -> Unit = {}): com.google.protobuf.conformance.ConformanceResponse { 
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = JspbEncodingConfig {
*    useJspbArrayAnyFormat = ...
* }
* ```
*/
operator fun com.google.protobuf.conformance.JspbEncodingConfig.Companion.invoke(body: com.google.protobuf.conformance.JspbEncodingConfigInternal.() -> Unit): com.google.protobuf.conformance.JspbEncodingConfig { 
    val msg = com.google.protobuf.conformance.JspbEncodingConfigInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    useJspbArrayAnyFormat = ...
* }
* ```
*/
fun com.google.protobuf.conformance.JspbEncodingConfig.copy(body: com.google.protobuf.conformance.JspbEncodingConfigInternal.() -> Unit = {}): com.google.protobuf.conformance.JspbEncodingConfig { 
    return this.asInternal().copyInternal(body)
}

/**
* Interface providing field-presence information for [com.google.protobuf.conformance.ConformanceRequest] messages.
* Retrieve it via the [com.google.protobuf.conformance.ConformanceRequest.presence] extension property.
*/
interface ConformanceRequestPresence { 
    val hasJspbEncodingOptions: kotlin.Boolean
}
