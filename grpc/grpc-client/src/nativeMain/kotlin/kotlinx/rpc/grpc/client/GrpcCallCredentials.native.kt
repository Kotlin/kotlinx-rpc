/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class)

package kotlinx.rpc.grpc.client

import cnames.structs.grpc_call_credentials
import kotlinx.cinterop.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.StatusException
import kotlinx.rpc.grpc.internal.destroyEntries
import kotlinx.rpc.grpc.internal.toRaw
import kotlinx.rpc.grpc.statusCode
import libkgrpc.*
import platform.posix.size_tVar

// Stable reference holder for Kotlin objects
private class CredentialsPluginState(
    val kotlinCreds: GrpcCallCredentials,
    val scope: CoroutineScope
)

private fun getMetadataCallback(
    state: COpaquePointer?,
    context: CValue<grpc_auth_metadata_context>,
    cb: grpc_credentials_plugin_metadata_cb?,
    user_data: COpaquePointer?,
    creds_md: CValuesRef<grpc_metadata>?,
    num_creds_md: CPointer<size_tVar>?,
    status: CPointer<grpc_status_code.Var>?,
    error_details: CPointer<CPointerVar<ByteVar>>?
): Int {
    val pluginState = state!!.asStableRef<CredentialsPluginState>().get()

    // Launch coroutine to call suspend function asynchronously
    pluginState.scope.launch {
        // Extract context information
        val serviceUrl = context.useContents { service_url?.toKString() ?: "" }
        val methodName = context.useContents { method_name?.toKString() ?: "" }
        val authority = extractAuthority(serviceUrl)

        // Create Kotlin context
        val kotlinContext = GrpcCallCredentials.Context(
            authority = authority,
            methodName = methodName,
        )

        var metadata = GrpcMetadata()
        var status = grpc_status_code.GRPC_STATUS_OK
        var errorDetails: String? = null
        try {
            // Call the Kotlin suspend function
            metadata = with(pluginState.kotlinCreds) {
                kotlinContext.getRequestMetadata()
            }
        } catch (e: StatusException) {
            status = e.getStatus().statusCode.toRaw()
            errorDetails = e.message
        } catch (e: Exception) {
            status = grpc_status_code.GRPC_STATUS_UNAUTHENTICATED
            errorDetails = e.message
        }

        // Convert GrpcMetadata to grpc_metadata array
        memScoped {
            val metadataArray = with(metadata) {
                this@memScoped.allocRawGrpcMetadata()
            }

            try {
                // Invoke the callback with success
                cb?.invoke(
                    user_data,
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

    // Return 0 to indicate asynchronous processing
    return 0
}

private fun debugStringCallback(state: COpaquePointer?): CPointer<ByteVar>? {
    return gpr_strdup("KotlinCallCredentials")
}

private fun extractAuthority(serviceUrl: String): String {
    // service_url format: "https://example.com:443/service"
    return serviceUrl
        .removePrefix("http://")
        .removePrefix("https://")
        .substringBefore("/")
}

private fun destroyCallback(state: COpaquePointer?) {
    state?.asStableRef<CredentialsPluginState>()?.dispose()
}

internal fun GrpcCallCredentials.createRaw(
    scope: CoroutineScope
): CPointer<grpc_call_credentials>? = memScoped {
    // Create stable reference to keep Kotlin object alive
    val pluginState = CredentialsPluginState(this@createRaw, scope)
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