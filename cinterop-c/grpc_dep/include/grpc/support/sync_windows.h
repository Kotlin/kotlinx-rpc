// Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

#ifndef GRPC_SUPPORT_SYNC_WINDOWS_H
#define GRPC_SUPPORT_SYNC_WINDOWS_H

#include <grpc/support/port_platform.h>

#ifdef GPR_WINDOWS

#include <grpc/support/sync_generic.h>

typedef struct {
  CRITICAL_SECTION cs; /* Not an SRWLock until Vista is unsupported */
  int locked;
} gpr_mu;

typedef CONDITION_VARIABLE gpr_cv;

typedef INIT_ONCE gpr_once;
#define GPR_ONCE_INIT INIT_ONCE_STATIC_INIT

#endif /* GPR_WINDOWS */

#endif /* GRPC_SUPPORT_SYNC_WINDOWS_H */
