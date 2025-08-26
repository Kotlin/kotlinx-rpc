// Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

#ifndef GRPC_SUPPORT_ATM_WINDOWS_H
#define GRPC_SUPPORT_ATM_WINDOWS_H

/** Win32 variant of atm_platform.h */
#include <grpc/support/port_platform.h>

#ifdef GPR_WINDOWS

typedef intptr_t gpr_atm;
#define GPR_ATM_MAX INTPTR_MAX
#define GPR_ATM_MIN INTPTR_MIN

#define gpr_atm_full_barrier MemoryBarrier

static __inline gpr_atm gpr_atm_acq_load(const gpr_atm* p) {
  gpr_atm result = *p;
  gpr_atm_full_barrier();
  return result;
}

static __inline gpr_atm gpr_atm_no_barrier_load(const gpr_atm* p) {
  /* TODO(dklempner): Can we implement something better here? */
  return gpr_atm_acq_load(p);
}

static __inline void gpr_atm_rel_store(gpr_atm* p, gpr_atm value) {
  gpr_atm_full_barrier();
  *p = value;
}

static __inline void gpr_atm_no_barrier_store(gpr_atm* p, gpr_atm value) {
  /* TODO(ctiller): Can we implement something better here? */
  gpr_atm_rel_store(p, value);
}

static __inline int gpr_atm_no_barrier_cas(gpr_atm* p, gpr_atm o, gpr_atm n) {
/** InterlockedCompareExchangePointerNoFence() not available on vista or
   windows7 */
#ifdef GPR_ARCH_64
  return o == (gpr_atm)InterlockedCompareExchangeAcquire64(
                  (volatile LONGLONG*)p, (LONGLONG)n, (LONGLONG)o);
#else
  return o == (gpr_atm)InterlockedCompareExchangeAcquire((volatile LONG*)p,
                                                         (LONG)n, (LONG)o);
#endif
}

static __inline int gpr_atm_acq_cas(gpr_atm* p, gpr_atm o, gpr_atm n) {
#ifdef GPR_ARCH_64
  return o == (gpr_atm)InterlockedCompareExchangeAcquire64(
                  (volatile LONGLONG*)p, (LONGLONG)n, (LONGLONG)o);
#else
  return o == (gpr_atm)InterlockedCompareExchangeAcquire((volatile LONG*)p,
                                                         (LONG)n, (LONG)o);
#endif
}

static __inline int gpr_atm_rel_cas(gpr_atm* p, gpr_atm o, gpr_atm n) {
#ifdef GPR_ARCH_64
  return o == (gpr_atm)InterlockedCompareExchangeRelease64(
                  (volatile LONGLONG*)p, (LONGLONG)n, (LONGLONG)o);
#else
  return o == (gpr_atm)InterlockedCompareExchangeRelease((volatile LONG*)p,
                                                         (LONG)n, (LONG)o);
#endif
}

static __inline int gpr_atm_full_cas(gpr_atm* p, gpr_atm o, gpr_atm n) {
#ifdef GPR_ARCH_64
  return o == (gpr_atm)InterlockedCompareExchange64((volatile LONGLONG*)p,
                                                    (LONGLONG)n, (LONGLONG)o);
#else
  return o == (gpr_atm)InterlockedCompareExchange((volatile LONG*)p, (LONG)n,
                                                  (LONG)o);
#endif
}

static __inline gpr_atm gpr_atm_no_barrier_fetch_add(gpr_atm* p,
                                                     gpr_atm delta) {
  /** Use the CAS operation to get pointer-sized fetch and add */
  gpr_atm old;
  do {
    old = *p;
  } while (!gpr_atm_no_barrier_cas(p, old, old + delta));
  return old;
}

static __inline gpr_atm gpr_atm_full_fetch_add(gpr_atm* p, gpr_atm delta) {
  /** Use a CAS operation to get pointer-sized fetch and add */
  gpr_atm old;
#ifdef GPR_ARCH_64
  do {
    old = *p;
  } while (old != (gpr_atm)InterlockedCompareExchange64((volatile LONGLONG*)p,
                                                        (LONGLONG)old + delta,
                                                        (LONGLONG)old));
#else
  do {
    old = *p;
  } while (old != (gpr_atm)InterlockedCompareExchange(
                      (volatile LONG*)p, (LONG)old + delta, (LONG)old));
#endif
  return old;
}

static __inline gpr_atm gpr_atm_full_xchg(gpr_atm* p, gpr_atm n) {
  return (gpr_atm)InterlockedExchangePointer((PVOID*)p, (PVOID)n);
}

#endif /* GPR_WINDOWS */

#endif /* GRPC_SUPPORT_ATM_WINDOWS_H */
