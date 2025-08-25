// Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

#ifndef GRPC_SUPPORT_LOG_WINDOWS_H
#define GRPC_SUPPORT_LOG_WINDOWS_H

#include <grpc/support/port_platform.h>

#ifdef __cplusplus
extern "C" {
#endif

/** Returns a string allocated with gpr_malloc that contains a UTF-8
 * formatted error message, corresponding to the error messageid.
 * Use in conjunction with GetLastError() et al.
 */
GPRAPI char* gpr_format_message(int messageid);

#ifdef __cplusplus
}
#endif

#endif /* GRPC_SUPPORT_LOG_WINDOWS_H */
