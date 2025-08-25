// Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

#ifndef GRPC_IMPL_CONNECTIVITY_STATE_H
#define GRPC_IMPL_CONNECTIVITY_STATE_H

// IWYU pragma: private, include <grpc/grpc.h>
// IWYU pragma: friend "src/.*"

#ifdef __cplusplus
extern "C" {
#endif

/** Connectivity state of a channel. */
typedef enum {
  /** channel is idle */
  GRPC_CHANNEL_IDLE,
  /** channel is connecting */
  GRPC_CHANNEL_CONNECTING,
  /** channel is ready for work */
  GRPC_CHANNEL_READY,
  /** channel has seen a failure but expects to recover */
  GRPC_CHANNEL_TRANSIENT_FAILURE,
  /** channel has seen a failure that it cannot recover from */
  GRPC_CHANNEL_SHUTDOWN
} grpc_connectivity_state;

#ifdef __cplusplus
}
#endif

#endif /* GRPC_IMPL_CONNECTIVITY_STATE_H */
