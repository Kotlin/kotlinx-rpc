// Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

#ifndef GRPC_SUPPORT_SYNC_CUSTOM_H
#define GRPC_SUPPORT_SYNC_CUSTOM_H

#include <grpc/support/port_platform.h>
#include <grpc/support/sync_generic.h>

/* Users defining GPR_CUSTOM_SYNC need to define the following macros. */

#ifdef GPR_CUSTOM_SYNC

typedef GPR_CUSTOM_MU_TYPE gpr_mu;
typedef GPR_CUSTOM_CV_TYPE gpr_cv;
typedef GPR_CUSTOM_ONCE_TYPE gpr_once;

#define GPR_ONCE_INIT GPR_CUSTOM_ONCE_INIT

#endif

#endif /* GRPC_SUPPORT_SYNC_CUSTOM_H */
