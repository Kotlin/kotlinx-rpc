// Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#ifndef GRPC_EVENT_ENGINE_ENDPOINT_CONFIG_H
#define GRPC_EVENT_ENGINE_ENDPOINT_CONFIG_H

#include <grpc/support/port_platform.h>

#include <optional>
#include <string>

#include "absl/strings/string_view.h"

namespace grpc_event_engine {
namespace experimental {

/// Collection of parameters used to configure client and server endpoints. The
/// \a EndpointConfig maps string-valued keys to values of type int,
/// string_view, or void pointer. Each EventEngine implementation should
/// document its set of supported configuration options.
class EndpointConfig {
 public:
  virtual ~EndpointConfig() = default;
  // If the key points to an integer config, an integer value gets returned.
  // Otherwise it returns an std::nullopt_t
  virtual std::optional<int> GetInt(absl::string_view key) const = 0;
  // If the key points to an string config, an string value gets returned.
  // Otherwise it returns an std::nullopt_t
  virtual std::optional<absl::string_view> GetString(
      absl::string_view key) const = 0;
  // If the key points to an void* config, a void* pointer value gets returned.
  // Otherwise it returns nullptr
  virtual void* GetVoidPointer(absl::string_view key) const = 0;
};

}  // namespace experimental
}  // namespace grpc_event_engine

#endif  // GRPC_EVENT_ENGINE_ENDPOINT_CONFIG_H
