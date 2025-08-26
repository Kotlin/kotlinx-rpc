// Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

#ifndef GRPC_IMPL_CALL_H
#define GRPC_IMPL_CALL_H

#include <grpc/grpc.h>
#include <grpc/support/port_platform.h>

#include "absl/functional/any_invocable.h"

// Run a callback in the call's EventEngine.
// Internal-only
void grpc_call_run_in_event_engine(const grpc_call* call,
                                   absl::AnyInvocable<void()> cb);

#endif /* GRPC_IMPL_CALL_H */
