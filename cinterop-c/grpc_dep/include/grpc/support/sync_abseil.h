// Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

#ifndef GRPC_SUPPORT_SYNC_ABSEIL_H
#define GRPC_SUPPORT_SYNC_ABSEIL_H

#include <grpc/support/port_platform.h>
#include <grpc/support/sync_generic.h>

#ifdef GPR_ABSEIL_SYNC

typedef intptr_t gpr_mu;
typedef intptr_t gpr_cv;
typedef int32_t gpr_once;

#define GPR_ONCE_INIT 0

#endif

#endif /* GRPC_SUPPORT_SYNC_ABSEIL_H */
