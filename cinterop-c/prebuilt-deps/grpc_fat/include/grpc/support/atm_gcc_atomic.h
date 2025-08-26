// Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

#ifndef GRPC_SUPPORT_ATM_GCC_ATOMIC_H
#define GRPC_SUPPORT_ATM_GCC_ATOMIC_H

// IWYU pragma: private, include <grpc/support/atm.h>

/* atm_platform.h for gcc and gcc-like compilers with the
   __atomic_* interface.  */
#include <grpc/support/port_platform.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef intptr_t gpr_atm;
#define GPR_ATM_MAX INTPTR_MAX
#define GPR_ATM_MIN INTPTR_MIN

#define gpr_atm_full_barrier() (__atomic_thread_fence(__ATOMIC_SEQ_CST))

#define gpr_atm_acq_load(p) (__atomic_load_n((p), __ATOMIC_ACQUIRE))
#define gpr_atm_no_barrier_load(p) (__atomic_load_n((p), __ATOMIC_RELAXED))
#define gpr_atm_rel_store(p, value) \
  (__atomic_store_n((p), (intptr_t)(value), __ATOMIC_RELEASE))
#define gpr_atm_no_barrier_store(p, value) \
  (__atomic_store_n((p), (intptr_t)(value), __ATOMIC_RELAXED))

#define gpr_atm_no_barrier_fetch_add(p, delta) \
  __atomic_fetch_add((p), (intptr_t)(delta), __ATOMIC_RELAXED)
#define gpr_atm_full_fetch_add(p, delta) \
  __atomic_fetch_add((p), (intptr_t)(delta), __ATOMIC_ACQ_REL)

static __inline int gpr_atm_no_barrier_cas(gpr_atm* p, gpr_atm o, gpr_atm n) {
  // Need to be c89 compatible, so we can't use false for the fourth argument.
  // NOLINTNEXTLINE(modernize-use-bool-literals)
  return __atomic_compare_exchange_n(p, &o, n, 0, __ATOMIC_RELAXED,
                                     __ATOMIC_RELAXED);
}

static __inline int gpr_atm_acq_cas(gpr_atm* p, gpr_atm o, gpr_atm n) {
  // Need to be c89 compatible, so we can't use false for the fourth argument.
  // NOLINTNEXTLINE(modernize-use-bool-literals)
  return __atomic_compare_exchange_n(p, &o, n, 0, __ATOMIC_ACQUIRE,
                                     __ATOMIC_RELAXED);
}

static __inline int gpr_atm_rel_cas(gpr_atm* p, gpr_atm o, gpr_atm n) {
  // Need to be c89 compatible, so we can't use false for the fourth argument.
  // NOLINTNEXTLINE(modernize-use-bool-literals)
  return __atomic_compare_exchange_n(p, &o, n, 0, __ATOMIC_RELEASE,
                                     __ATOMIC_RELAXED);
}

static __inline int gpr_atm_full_cas(gpr_atm* p, gpr_atm o, gpr_atm n) {
  // Need to be c89 compatible, so we can't use false for the fourth argument.
  // NOLINTNEXTLINE(modernize-use-bool-literals)
  return __atomic_compare_exchange_n(p, &o, n, 0, __ATOMIC_ACQ_REL,
                                     __ATOMIC_RELAXED);
}

#define gpr_atm_full_xchg(p, n) __atomic_exchange_n((p), (n), __ATOMIC_ACQ_REL)

#ifdef __cplusplus
}
#endif

#endif /* GRPC_SUPPORT_ATM_GCC_ATOMIC_H */
