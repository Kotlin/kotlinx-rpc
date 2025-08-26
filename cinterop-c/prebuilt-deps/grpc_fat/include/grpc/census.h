// Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

#ifndef GRPC_CENSUS_H
#define GRPC_CENSUS_H

#include <grpc/grpc.h>
#include <grpc/support/port_platform.h>

#ifdef __cplusplus
extern "C" {
#endif

/**
  A Census Context is a handle used by Census to represent the current tracing
  and stats collection information. Contexts should be propagated across RPC's
  (this is the responsibility of the local RPC system). */
typedef struct census_context census_context;

#ifdef __cplusplus
}
#endif

#endif /* GRPC_CENSUS_H */
