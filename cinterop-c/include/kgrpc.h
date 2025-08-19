/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

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

/*
 * Call to grpc_iomgr_run_in_background(), which is not exposed as extern "C" and therefore must be wrapped.
 */
bool kgrpc_iomgr_run_in_background();

#ifdef __cplusplus
    }
#endif

#endif //GRPCPP_C_H
