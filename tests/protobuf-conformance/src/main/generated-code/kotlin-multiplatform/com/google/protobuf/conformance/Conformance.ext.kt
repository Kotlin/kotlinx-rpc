@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.conformance

import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
* Constructs a new message.
* ```
* val message = TestStatus {
*    name = ...
* }
* ```
*/
operator fun TestStatus.Companion.invoke(body: TestStatusInternal.() -> Unit): TestStatus {
    val msg = TestStatusInternal().apply(body)
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
fun TestStatus.copy(body: TestStatusInternal.() -> Unit = {}): TestStatus {
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
operator fun FailureSet.Companion.invoke(body: FailureSetInternal.() -> Unit): FailureSet {
    val msg = FailureSetInternal().apply(body)
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
fun FailureSet.copy(body: FailureSetInternal.() -> Unit = {}): FailureSet {
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
operator fun ConformanceRequest.Companion.invoke(body: ConformanceRequestInternal.() -> Unit): ConformanceRequest {
    val msg = ConformanceRequestInternal().apply(body)
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
fun ConformanceRequest.copy(body: ConformanceRequestInternal.() -> Unit = {}): ConformanceRequest {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.conformance.ConformanceRequest] instance.
*/
val ConformanceRequest.presence: ConformanceRequestPresence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = ConformanceResponse {
*    result = ...
* }
* ```
*/
operator fun ConformanceResponse.Companion.invoke(body: ConformanceResponseInternal.() -> Unit): ConformanceResponse {
    val msg = ConformanceResponseInternal().apply(body)
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
fun ConformanceResponse.copy(body: ConformanceResponseInternal.() -> Unit = {}): ConformanceResponse {
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
operator fun JspbEncodingConfig.Companion.invoke(body: JspbEncodingConfigInternal.() -> Unit): JspbEncodingConfig {
    val msg = JspbEncodingConfigInternal().apply(body)
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
fun JspbEncodingConfig.copy(body: JspbEncodingConfigInternal.() -> Unit = {}): JspbEncodingConfig {
    return this.asInternal().copyInternal(body)
}

/**
* Interface providing field-presence information for [com.google.protobuf.conformance.ConformanceRequest] messages.
* Retrieve it via the [com.google.protobuf.conformance.ConformanceRequest.presence] extension property.
*/
interface ConformanceRequestPresence {
    val hasJspbEncodingOptions: Boolean
}
