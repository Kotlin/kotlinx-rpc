load("@rules_cc//cc:defs.bzl", "cc_library")

def grpc_shim_target(name):
    prebuilt_root = "build/grpc/%s/prebuilt" % name
    headers_root = "%s/headers" % prebuilt_root
    virtual_import_includes = native.glob(
        ["%s/**/_virtual_imports/*" % headers_root],
        allow_empty = True,
        exclude_directories = 0,
    )

    cc_library(
        name = "grpc_prebuilt_%s" % name,
        # The grpc shim only needs grpc's published headers to compile. The grpc archives are added later when cinterop
        # bundles the final klib, so Bazel does not need to model them as part of this compile-time dependency.
        hdrs = native.glob(
            [
                "%s/**" % headers_root,
            ],
            allow_empty = True,
            exclude_directories = 1,
        ),
        includes = [
            "%s/include" % headers_root,
            headers_root,
        ] + virtual_import_includes,
        visibility = ["//visibility:private"],
    )

    cc_library(
        name = "grpc_shim_%s" % name,
        srcs = ["src/cpp/kgrpc.cpp"],
        hdrs = ["include/kgrpc.h"],
        includes = ["include"],
        visibility = ["//visibility:public"],
        deps = [":grpc_prebuilt_%s" % name],
    )
