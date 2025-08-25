// Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

#ifndef GRPC_FORK_H
#define GRPC_FORK_H

#include <grpc/support/port_platform.h>

/**
 * gRPC applications should call this before calling fork().  There should be no
 * active gRPC function calls between calling grpc_prefork() and
 * grpc_postfork_parent()/grpc_postfork_child().
 *
 *
 * Typical use:
 * grpc_prefork();
 * int pid = fork();
 * if (pid) {
 *  grpc_postfork_parent();
 *  // Parent process..
 * } else {
 *  grpc_postfork_child();
 *  // Child process...
 * }
 */

void grpc_prefork(void);

void grpc_postfork_parent(void);

void grpc_postfork_child(void);

void grpc_fork_handlers_auto_register(void);

#endif /* GRPC_FORK_H */
