// Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

#ifndef GRPC_SUPPORT_SYNC_GENERIC_H
#define GRPC_SUPPORT_SYNC_GENERIC_H

/* Generic type definitions for gpr_sync. */

#include <grpc/support/atm.h>
#include <grpc/support/port_platform.h>

/* gpr_event */
typedef struct {
  gpr_atm state;
} gpr_event;

#define GPR_EVENT_INIT {0}

/* gpr_refcount */
typedef struct {
  gpr_atm count;
} gpr_refcount;

/* gpr_stats_counter */
typedef struct {
  gpr_atm value;
} gpr_stats_counter;

#define GPR_STATS_INIT {0}

#endif /* GRPC_SUPPORT_SYNC_GENERIC_H */
