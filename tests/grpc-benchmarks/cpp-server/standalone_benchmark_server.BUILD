# Added to the pinned grpc/grpc test/cpp/qps package by build.sh.
grpc_cc_binary(
    name = "standalone_benchmark_server",
    srcs = ["standalone_benchmark_server.cc"],
    external_deps = [
        "absl/flags:flag",
        "absl/log:log",
    ],
    deps = [
        ":qps_worker_impl",
        "//:gpr",
        "//:grpc",
        "//:grpc++",
        "//src/proto/grpc/testing:control_cc_proto",
        "//test/core/test_util:grpc_test_util",
        "//test/cpp/util:test_config",
        "//test/cpp/util:test_util",
    ],
)
