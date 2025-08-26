// Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

#ifndef GRPC_GRPC_AUDIT_LOGGING_H
#define GRPC_GRPC_AUDIT_LOGGING_H

#include <grpc/support/json.h>
#include <grpc/support/port_platform.h>

#include <memory>
#include <string>

#include "absl/status/statusor.h"
#include "absl/strings/string_view.h"

namespace grpc_core {
namespace experimental {

// The class containing the context for an audited RPC.
class AuditContext {
 public:
  AuditContext(absl::string_view rpc_method, absl::string_view principal,
               absl::string_view policy_name, absl::string_view matched_rule,
               bool authorized)
      : rpc_method_(rpc_method),
        principal_(principal),
        policy_name_(policy_name),
        matched_rule_(matched_rule),
        authorized_(authorized) {}

  absl::string_view rpc_method() const { return rpc_method_; }
  absl::string_view principal() const { return principal_; }
  absl::string_view policy_name() const { return policy_name_; }
  absl::string_view matched_rule() const { return matched_rule_; }
  bool authorized() const { return authorized_; }

 private:
  absl::string_view rpc_method_;
  absl::string_view principal_;
  absl::string_view policy_name_;
  absl::string_view matched_rule_;
  bool authorized_;
};

// This base class for audit logger implementations.
class AuditLogger {
 public:
  virtual ~AuditLogger() = default;
  virtual absl::string_view name() const = 0;
  virtual void Log(const AuditContext& audit_context) = 0;
};

// This is the base class for audit logger factory implementations.
class AuditLoggerFactory {
 public:
  class Config {
   public:
    virtual ~Config() = default;
    virtual absl::string_view name() const = 0;
    virtual std::string ToString() const = 0;
  };

  virtual ~AuditLoggerFactory() = default;
  virtual absl::string_view name() const = 0;

  virtual absl::StatusOr<std::unique_ptr<Config>> ParseAuditLoggerConfig(
      const Json& json) = 0;

  virtual std::unique_ptr<AuditLogger> CreateAuditLogger(
      std::unique_ptr<AuditLoggerFactory::Config>) = 0;
};

// Registers an audit logger factory. This should only be called during
// initialization.
void RegisterAuditLoggerFactory(std::unique_ptr<AuditLoggerFactory> factory);

}  // namespace experimental
}  // namespace grpc_core

#endif  // GRPC_GRPC_AUDIT_LOGGING_H
