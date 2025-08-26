// Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

#ifndef GRPC_SUPPORT_STRING_UTIL_H
#define GRPC_SUPPORT_STRING_UTIL_H

#include <grpc/support/port_platform.h>
#include <grpc/support/time.h>

#ifdef __cplusplus
extern "C" {
#endif

/** String utility functions */

/** Returns a copy of src that can be passed to gpr_free().
   If allocation fails or if src is NULL, returns NULL. */
GPRAPI char* gpr_strdup(const char* src);

/** printf to a newly-allocated string.  The set of supported formats may vary
   between platforms.

   On success, returns the number of bytes printed (excluding the final '\0'),
   and *strp points to a string which must later be destroyed with gpr_free().

   On error, returns -1 and sets *strp to NULL. If the format string is bad,
   the result is undefined. */
GPRAPI int gpr_asprintf(char** strp, const char* format, ...)
    GPR_PRINT_FORMAT_CHECK(2, 3);

#ifdef __cplusplus
}
#endif

#endif /* GRPC_SUPPORT_STRING_UTIL_H */
