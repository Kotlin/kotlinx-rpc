/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class)

package kotlinx.rpc.grpc.client

import cnames.structs.grpc_call_credentials
import kotlinx.cinterop.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.StatusException
import kotlinx.rpc.grpc.internal.destroyEntries
import kotlinx.rpc.grpc.internal.toRaw
import kotlinx.rpc.grpc.statusCode
import libkgrpc.*
import platform.posix.size_tVar
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

// Stable reference holder for Kotlin objects
private class CredentialsPluginState(
    val kotlinCreds: GrpcCallCredentials,
    val coroutineContext: CoroutineContext,
)

private fun getMetadataCallback(
    state: COpaquePointer?,
    context: CValue<grpc_auth_metadata_context>,
    cb: grpc_credentials_plugin_metadata_cb?,
    userData: COpaquePointer?,
    credsMd: CPointer<grpc_metadata>?,
    numCredMd: CPointer<size_tVar>?,
    status: CPointer<grpc_status_code.Var>?,
    errorDetails: CPointer<CPointerVar<ByteVar>>?
): Int {
    val pluginState = state!!.asStableRef<CredentialsPluginState>().get()

    fun notifyResult(metadata: GrpcMetadata, status: grpc_status_code, errorDetails: String?) {
        memScoped {
            // Convert GrpcMetadata to grpc_metadata array
            val metadataArray = with(metadata) {
                this@memScoped.allocRawGrpcMetadata()
            }

            try {
                // Invoke the callback with success
                cb?.invoke(
                    userData,
                    metadataArray.metadata,
                    metadataArray.count,
                    status,
                    errorDetails?.cstr?.ptr
                )
            } finally {
                metadataArray.destroyEntries()
            }
        }
    }

    val scope = CoroutineScope(pluginState.coroutineContext)

    // Launch coroutine to call a suspend function asynchronously
    scope.launch {
        // Extract context information
        val serviceUrl = context.useContents { service_url?.toKString() ?: "" }
        val methodName = context.useContents { method_name?.toKString() ?: "" }
        val authority = extractAuthority(serviceUrl)
        val serviceFq = serviceUrl.removeUntil("$authority/")

        // Create Kotlin context
        val kotlinContext = GrpcCallCredentials.Context(
            authority = authority,
            methodName = "$serviceFq/$methodName"
        )

        var metadata = GrpcMetadata()
        try {
            // Call the Kotlin suspend function
            metadata = with(pluginState.kotlinCreds) {
                kotlinContext.getRequestMetadata()
            }
            notifyResult(metadata, grpc_status_code.GRPC_STATUS_OK, null)
        } catch (e: StatusException) {
            notifyResult(metadata, e.getStatus().statusCode.toRaw(), e.message)
        } catch (e: CancellationException) {
            notifyResult(metadata, grpc_status_code.GRPC_STATUS_CANCELLED, e.message)
            throw e
        } catch (e: Exception) {
            notifyResult(metadata, grpc_status_code.GRPC_STATUS_UNAVAILABLE, e.message)
        }
    }

    // Return 0 to indicate asynchronous processing
    return 0
}

private fun debugStringCallback(state: COpaquePointer?): CPointer<ByteVar>? {
    return gpr_strdup("KotlinCallCredentials")
}

private fun extractAuthority(serviceUrl: String): String {
    // service_url format: "://example.com:443/with.package.service"
    return serviceUrl
        .removeUntil("://")
        .substringBefore("/")
}

private fun String.removeUntil(pattern: String): String {
    val idx = indexOf(pattern)
    return if (idx == -1) this else removeRange(0, idx + pattern.length)
}

private fun destroyCallback(state: COpaquePointer?) {
    state?.asStableRef<CredentialsPluginState>()?.dispose()
}

internal fun GrpcCallCredentials.createRaw(
    coroutineContext: CoroutineContext,
): CPointer<grpc_call_credentials>? = memScoped {
    // Create a stable reference to keep the Kotlin object alive
    val pluginState = CredentialsPluginState(this@createRaw, coroutineContext)
    val stableRef = StableRef.create(pluginState)

    // Create plugin structure
    val plugin = alloc<grpc_metadata_credentials_plugin> {
        get_metadata = staticCFunction(::getMetadataCallback)
        debug_string = staticCFunction(::debugStringCallback)
        destroy = staticCFunction(::destroyCallback)
        state = stableRef.asCPointer()
        type = "kgrpc_call_credentials".cstr.ptr
    }

    // Determine security level
    val minSecurityLevel = if (this@createRaw.requiresTransportSecurity) {
        GRPC_PRIVACY_AND_INTEGRITY
    } else {
        GRPC_SECURITY_NONE
    }

    // Create and return credentials
    grpc_metadata_credentials_create_from_plugin(
        plugin.readValue(),
        minSecurityLevel,
        null
    )
}