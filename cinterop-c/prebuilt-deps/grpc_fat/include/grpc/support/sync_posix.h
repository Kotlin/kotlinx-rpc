// Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

#ifndef GRPC_SUPPORT_SYNC_POSIX_H
#define GRPC_SUPPORT_SYNC_POSIX_H

#include <grpc/support/port_platform.h>
#include <grpc/support/sync_generic.h>
#include <pthread.h>

#ifdef GRPC_ASAN_ENABLED
/* The member |leak_checker| is used to check whether there is a memory leak
 * caused by upper layer logic that's missing the |gpr_xx_destroy| call
 * to the object before freeing it.
 * This issue was reported at https://github.com/grpc/grpc/issues/17563
 * and discussed at https://github.com/grpc/grpc/pull/17586
 */
typedef struct {
  pthread_mutex_t mutex;
  int* leak_checker;
} gpr_mu;

typedef struct {
  pthread_cond_t cond_var;
  int* leak_checker;
} gpr_cv;
#else
typedef pthread_mutex_t gpr_mu;
typedef pthread_cond_t gpr_cv;
#endif
typedef pthread_once_t gpr_once;

#define GPR_ONCE_INIT PTHREAD_ONCE_INIT

#endif /* GRPC_SUPPORT_SYNC_POSIX_H */
