load(
    "@bazel_tools//tools/cpp:cc_toolchain_config_lib.bzl",
    "feature",
    "flag_group",
    "flag_set",
    "tool_path",
)

def _impl(ctx):
    tool_paths = [
        tool_path(name = "gcc", path = "run_konan_clang.sh"),
        tool_path(name = "cpp", path = "run_konan_clang.sh"),
        tool_path(name = "ar", path = "run_konan_ar.sh"),
        tool_path(name = "ld", path = "/usr/bin/false"),
        tool_path(name = "nm", path = "/usr/bin/true"),
        tool_path(name = "objdump", path = "/usr/bin/true"),
        tool_path(name = "strip", path = "/usr/bin/true"),
    ]

    deps = ctx.var.get("KONAN_DEPS")
    if not deps:
        fail("Set --define=KONAN_DEPS=/path/to/.konan/dependencies")

    return cc_common.create_cc_toolchain_config_info(
        ctx = ctx,
        toolchain_identifier = "konan_linux_x64",
        abi_libc_version = "glibc",
        abi_version = "glibc",
        compiler = "clang",
        host_system_name = "local",
        target_cpu = "x86_64",
        target_system_name = "linux",
        tool_paths = tool_paths,
        cxx_builtin_include_directories = [
            deps + "/apple-llvm-20200714-macos-aarch64-essentials/lib/clang/11.1.0/include",
            deps + "/llvm-19-aarch64-macos-essentials-75/lib/clang/19/include",
            deps + "/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/lib/gcc/x86_64-unknown-linux-gnu/8.3.0/include",
            deps + "/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/x86_64-unknown-linux-gnu/include/c++/8.3.0",
            deps + "/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/x86_64-unknown-linux-gnu/include/c++/8.3.0/x86_64-unknown-linux-gnu",
            deps + "/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/x86_64-unknown-linux-gnu/sysroot/usr/include",
        ],
    )

cc_toolchain_config = rule(
    implementation = _impl,
    attrs = {},
    provides = [CcToolchainConfigInfo],
)
