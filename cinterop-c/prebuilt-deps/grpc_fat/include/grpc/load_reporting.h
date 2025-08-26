// Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

#ifndef GRPC_LOAD_REPORTING_H
#define GRPC_LOAD_REPORTING_H

#include <grpc/support/port_platform.h>

#ifdef __cplusplus
extern "C" {
#endif

/** Metadata key for the gRPC LB load balancer token.
 *
 * The value corresponding to this key is an opaque token that is given to the
 * frontend as part of each pick; the frontend sends this token to the backend
 * in each request it sends when using that pick. The token is used by the
 * backend to verify the request and to allow the backend to report load to the
 * gRPC LB system. */
#define GRPC_LB_TOKEN_MD_KEY "lb-token"

/** Metadata key for gRPC LB cost reporting.
 *
 * The value corresponding to this key is an opaque binary blob reported by the
 * backend as part of its trailing metadata containing cost information for the
 * call. */
#define GRPC_LB_COST_MD_KEY "lb-cost-bin"

#ifdef __cplusplus
}
#endif

#endif /* GRPC_LOAD_REPORTING_H */
