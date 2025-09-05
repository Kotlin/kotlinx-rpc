// Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

/*
 * Helper functions required for gRPC Core cinterop.
 */

#ifndef GRPCPP_C_H
#define GRPCPP_C_H

#include <stdbool.h>
#include <grpc/grpc.h>

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Struct that layouts a grpc_completion_queue_functor and user opaque data pointer,
 * to implement the callback mechanism in the K/N CompletionQueue.
 */
typedef struct {
    grpc_completion_queue_functor functor;
    void *user_data;
} kgrpc_cb_tag;


// This is a duplicate of the RegisteredCallAllocation, which is defined in
// https://github.com/grpc/grpc/blob/893bdadd56dbb75fb156175afdaa2b0d47e1c15b/src/core/server/server.h#L150-L157.
// This is required, as RegisteredCallAllocation is not part of the exposed C API.
typedef struct {
    void *tag;
    grpc_call **call;
    grpc_metadata_array *initial_metadata;
    gpr_timespec *deadline;
    grpc_byte_buffer **optional_payload;
    grpc_completion_queue *cq;
} kgrpc_registered_call_allocation;

typedef struct {
    void* tag;
    grpc_call** call;
    grpc_metadata_array* initial_metadata;
    grpc_call_details* details;
    grpc_completion_queue* cq;
} kgrpc_batch_call_allocation;

typedef kgrpc_registered_call_allocation (*kgrpc_registered_call_allocator)(void* ctx);
typedef kgrpc_batch_call_allocation (*kgrpc_batch_call_allocator)(void* ctx);

/*
* Call to grpc_iomgr_run_in_background(), which is not exposed as extern "C" and therefore must be wrapped.
*/
bool kgrpc_iomgr_run_in_background();

/**
 * Registers a C-style allocator callback for accepting gRPC calls to a specific method.
 *
 * Wraps the internal C++ API `Server::SetRegisteredMethodAllocator()` to enable
 * callback-driven method dispatch via the Core C API.
 *
 * When the gRPC Core needs to accept a new call for the specified method, it invokes:
 *   kgrpc_registered_call_allocation alloc = allocator();
 * to retrieve the accept context, including `tag`, `grpc_call*`, metadata, deadline,
 * optional payload, and the completion queue.
 *
 * @param server         The gRPC C `grpc_server*` instance.
 * @param cq             A callback-style `grpc_completion_queue*` (must be registered earlier).
 * @param method_tag     Opaque identifier from `grpc_server_register_method()` for the RPC method.
 * @param allocator_ctx  The context for the callback to pass all necessary objects to the static function.
 * @param allocator      Function providing new accept contexts (`kgrpc_registered_call_allocation`).
 */
void kgrpc_server_set_register_method_allocator(
        grpc_server *server,
        grpc_completion_queue *cq,
        void *method_tag,
        void *allocator_ctx,
        kgrpc_registered_call_allocator allocator
);

/**
 * Like kgrpc_server_set_register_method_allocator but instead of registered methods,
 * it sets an allocation callback for unknown method calls.
 */
void kgrpc_server_set_batch_method_allocator(
    grpc_server *server,
    grpc_completion_queue *cq,
    void *allocator_ctx,
    kgrpc_batch_call_allocator allocator
);


#ifdef __cplusplus
}
#endif

#endif //GRPCPP_C_H
