/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

#include <grpc/impl/channel_arg_names.h>
#include <grpc/support/time.h>
#include <signal.h>

#include <cstdint>
#include <memory>
#include <string>

#include "src/proto/grpc/testing/control.pb.h"
#include "test/core/test_util/test_config.h"
#include "test/cpp/qps/server.h"
#include "test/cpp/util/test_config.h"
#include "absl/flags/flag.h"
#include "absl/log/log.h"

ABSL_FLAG(int32_t, port, 50051, "Port on which the benchmark server listens");
ABSL_FLAG(std::string, server_type, "async",
          "C++ server implementation: async, callback, or sync");
ABSL_FLAG(int32_t, max_message_bytes, 32 * 1024 * 1024,
          "Maximum request and response message size");

namespace {

volatile sig_atomic_t stop_requested = 0;

void HandleSignal(int /* signal */) { stop_requested = 1; }

std::unique_ptr<grpc::testing::Server> CreateServer(
    const grpc::testing::ServerConfig& config,
    const std::string& server_type) {
  if (server_type == "async") {
    return grpc::testing::CreateAsyncServer(config);
  }
  if (server_type == "callback") {
    return grpc::testing::CreateCallbackServer(config);
  }
  if (server_type == "sync") {
    return grpc::testing::CreateSynchronousServer(config);
  }

  LOG(ERROR) << "Unknown server type '" << server_type
             << "'; expected async, callback, or sync";
  return nullptr;
}

}  // namespace

int main(int argc, char** argv) {
  grpc::testing::TestEnvironment environment(&argc, argv);
  grpc::testing::InitTest(&argc, &argv, true);

  grpc::testing::ServerConfig config;
  config.set_port(absl::GetFlag(FLAGS_port));
  config.set_protocol(grpc::testing::Protocol::HTTP2);
  const int32_t max_message_bytes =
      absl::GetFlag(FLAGS_max_message_bytes);
  for (const char* name : {GRPC_ARG_MAX_RECEIVE_MESSAGE_LENGTH,
                           GRPC_ARG_MAX_SEND_MESSAGE_LENGTH}) {
    grpc::testing::ChannelArg* argument = config.add_channel_args();
    argument->set_name(name);
    argument->set_int_value(max_message_bytes);
  }

  const std::string server_type = absl::GetFlag(FLAGS_server_type);
  std::unique_ptr<grpc::testing::Server> server =
      CreateServer(config, server_type);
  if (server == nullptr) {
    return 1;
  }

  signal(SIGINT, HandleSignal);
  signal(SIGTERM, HandleSignal);

  LOG(INFO) << "C++ benchmark server listening on [::]:" << server->port()
            << " using the " << server_type << " implementation"
            << " with a " << max_message_bytes << " byte message limit";

  while (!stop_requested) {
    gpr_sleep_until(gpr_time_add(gpr_now(GPR_CLOCK_REALTIME),
                                 gpr_time_from_millis(250, GPR_TIMESPAN)));
  }

  return 0;
}
