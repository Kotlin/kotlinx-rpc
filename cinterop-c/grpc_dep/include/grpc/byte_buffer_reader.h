// Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

#ifndef GRPC_BYTE_BUFFER_READER_H
#define GRPC_BYTE_BUFFER_READER_H

#include <grpc/support/port_platform.h>

#ifdef __cplusplus
extern "C" {
#endif

struct grpc_byte_buffer;

struct grpc_byte_buffer_reader {
  struct grpc_byte_buffer* buffer_in;
  struct grpc_byte_buffer* buffer_out;
  /** Different current objects correspond to different types of byte buffers */
  union grpc_byte_buffer_reader_current {
    /** Index into a slice buffer's array of slices */
    unsigned index;
  } current;
};

#ifdef __cplusplus
}
#endif

#endif /* GRPC_BYTE_BUFFER_READER_H */
