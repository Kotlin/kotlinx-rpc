// Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

#ifndef GRPC_EVENT_ENGINE_INTERNAL_WRITE_EVENT_H
#define GRPC_EVENT_ENGINE_INTERNAL_WRITE_EVENT_H

namespace grpc_event_engine::experimental::internal {

// Use of this enum via this name is internal to gRPC.
// API users should get this enumeration via the
// EventEngine::Endpoint::WriteEvent.
enum class WriteEvent {
  kSendMsg,
  kScheduled,
  kSent,
  kAcked,
  kClosed,
  kCount  // Must be last.
};

}  // namespace grpc_event_engine::experimental::internal

#endif  // GRPC_EVENT_ENGINE_INTERNAL_WRITE_EVENT_H
