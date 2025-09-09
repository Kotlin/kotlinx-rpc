// Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

#include <kgrpc.h>
#include "src/core/lib/iomgr/iomgr.h"
#include "src/core/server/server.h"

extern "C" {

bool kgrpc_iomgr_run_in_background() {
    return grpc_iomgr_run_in_background();
}

void kgrpc_server_set_register_method_allocator(
        grpc_server *server,
        grpc_completion_queue *cq,
        void *method_tag,
        void *allocator_ctx,
        kgrpc_registered_call_allocator allocator
) {
    grpc_core::Server::FromC(server)->SetRegisteredMethodAllocator(
            cq,
            method_tag,
            [allocator_ctx, allocator] {
                auto result = allocator(allocator_ctx);
                return grpc_core::Server::RegisteredCallAllocation{
                        .tag = result.tag,
                        .call = result.call,
                        .initial_metadata = result.initial_metadata,
                        .deadline = result.deadline,
                        .optional_payload = result.optional_payload,
                        .cq = result.cq,
                };
            });
}

void kgrpc_server_set_batch_method_allocator(
    grpc_server *server,
    grpc_completion_queue *cq,
    void *allocator_ctx,
    kgrpc_batch_call_allocator allocator
) {
    grpc_core::Server::FromC(server)->SetBatchMethodAllocator(
            cq,
            [allocator_ctx, allocator] {
                auto result = allocator(allocator_ctx);
                return grpc_core::Server::BatchCallAllocation{
                        .tag = result.tag,
                        .call = result.call,
                        .initial_metadata = result.initial_metadata,
                        .details = result.details,
                        .cq = result.cq,
                };
            });
}

}


