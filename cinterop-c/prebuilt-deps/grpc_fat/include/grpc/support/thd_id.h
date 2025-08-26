// Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

#ifndef GRPC_SUPPORT_THD_ID_H
#define GRPC_SUPPORT_THD_ID_H
/** Thread ID interface for GPR.

   Used by some wrapped languages for logging purposes.

   Types
        gpr_thd_id        a unique opaque identifier for a thread.
 */

#include <grpc/support/port_platform.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef uintptr_t gpr_thd_id;

/** Returns the identifier of the current thread. */
GPRAPI gpr_thd_id gpr_thd_currentid(void);

#ifdef __cplusplus
}
#endif

#endif /* GRPC_SUPPORT_THD_ID_H */
