// Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

#ifndef GRPC_IMPL_PROPAGATION_BITS_H
#define GRPC_IMPL_PROPAGATION_BITS_H

// IWYU pragma: private

#include <grpc/support/port_platform.h>

#ifdef __cplusplus
extern "C" {
#endif

/** Propagation bits: this can be bitwise or-ed to form propagation_mask for
 * grpc_call */
/** Propagate deadline */
#define GRPC_PROPAGATE_DEADLINE ((uint32_t)1)
/** Propagate census context */
#define GRPC_PROPAGATE_CENSUS_STATS_CONTEXT ((uint32_t)2)
#define GRPC_PROPAGATE_CENSUS_TRACING_CONTEXT ((uint32_t)4)
/** Propagate cancellation */
#define GRPC_PROPAGATE_CANCELLATION ((uint32_t)8)

/** Default propagation mask: clients of the core API are encouraged to encode
   deltas from this in their implementations... ie write:
   GRPC_PROPAGATE_DEFAULTS & ~GRPC_PROPAGATE_DEADLINE to disable deadline
   propagation. Doing so gives flexibility in the future to define new
   propagation types that are default inherited or not. */
#define GRPC_PROPAGATE_DEFAULTS                                                \
  ((uint32_t)((                                                                \
      0xffff | GRPC_PROPAGATE_DEADLINE | GRPC_PROPAGATE_CENSUS_STATS_CONTEXT | \
      GRPC_PROPAGATE_CENSUS_TRACING_CONTEXT | GRPC_PROPAGATE_CANCELLATION)))

#ifdef __cplusplus
}
#endif

#endif /* GRPC_IMPL_PROPAGATION_BITS_H */
