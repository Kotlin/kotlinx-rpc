load("@bazel_tools//tools/build_defs/cc:action_names.bzl", "ACTION_NAMES")
load(
    "@bazel_tools//tools/cpp:cc_toolchain_config_lib.bzl",
    "env_entry",
    "env_set",
    "feature",
    "flag_group",
    "flag_set",
    "tool_path",
    "with_feature_set",
)

ALL_ACTIONS = [
    ACTION_NAMES.c_compile,
    ACTION_NAMES.cpp_compile,
    ACTION_NAMES.assemble,
    ACTION_NAMES.preprocess_assemble,
    ACTION_NAMES.cpp_link_executable,
    ACTION_NAMES.cpp_link_dynamic_library,
    ACTION_NAMES.cpp_link_nodeps_dynamic_library,
    ACTION_NAMES.cpp_link_static_library,
]

# Constructs the toolchain configuration by defining all necessary flags and toolchain wrapper scripts.
# The wrapper scripts call Konan which delegates all requests to the corresponding clang/llvm tools.
# Additionally it includes all built-in include directories from the Konan toolchain and sysroot.
def _impl(ctx):
    target = ctx.attr.target
    home = ctx.var.get("KONAN_HOME")
    if not home:
        fail("Set --define=KONAN_HOME=/path/to/.konan/kotlin-native-prebuilt-*")
    deps = home + "/../dependencies"

    if target == "linux_x64":
        cpu = "x86_64"
    elif target == "linux_arm64":
        cpu = "aarch64"
    else:
        fail("Unsupported target: {}".format(target))

    tool_paths = [
        tool_path(name = "gcc", path = "run_konan_clang.sh"),
        tool_path(name = "cpp", path = "run_konan_clangxx.sh"),
        tool_path(name = "ar", path = "run_konan_ar.sh"),
        tool_path(name = "ld", path = "/usr/bin/false"),
        tool_path(name = "nm", path = "/usr/bin/false"),
        tool_path(name = "objdump", path = "/usr/bin/false"),
        tool_path(name = "strip", path = "/usr/bin/false"),
    ]

    features = [
        feature(
            name = "export_konan_home",
            enabled = True,
            env_sets = [env_set(
                actions = ALL_ACTIONS,
                env_entries = [
                    env_entry(key = "KONAN_HOME", value = home),
                    env_entry(key = "KONAN_TARGET", value = target),
                ],
            )],
        ),

        # One feature, multiple flag_sets: common + per-mode.
        feature(
            name = "konan_compile",
            enabled = True,
            flag_sets = [
                # Common C/C++ flags (always applied)
                flag_set(
                    actions = [
                        ACTION_NAMES.c_compile,
                        ACTION_NAMES.cpp_compile,
                        ACTION_NAMES.assemble,
                        ACTION_NAMES.preprocess_assemble,
                    ],
                    flag_groups = [flag_group(flags = [
                        # Reproducibility / diagnostics
                        "-no-canonical-prefixes",
                        "-Wno-builtin-macro-redefined",
                        '-D__DATE__="redacted"',
                        '-D__TIMESTAMP__="redacted"',
                        '-D__TIME__="redacted"',
                        "-fcolor-diagnostics",

                        "-U_FORTIFY_SOURCE",
                        "-fstack-protector",
                        "-fno-omit-frame-pointer",

                        "-Wall",
                        "-Wthread-safety",
                        "-Wself-assign",

                        "--target=" + ("x86_64-unknown-linux-gnu" if target == "linux_x64" else "aarch64-unknown-linux-gnu"),
                    ])],
                ),
                # C++-only flags
                flag_set(
                    actions = [ACTION_NAMES.cpp_compile],
                    flag_groups = [flag_group(flags = [
                        "-std=c++17",
                        "-stdlib=libstdc++",
                    ])],
                ),
                # dbg mode
                flag_set(
                    actions = [ACTION_NAMES.c_compile, ACTION_NAMES.cpp_compile],
                    with_features = [with_feature_set(features = ["dbg"])],
                    flag_groups = [flag_group(flags = [
                        "-g",
                        "-fstandalone-debug",
                    ])],
                ),
                # opt mode
                flag_set(
                    actions = [ACTION_NAMES.c_compile, ACTION_NAMES.cpp_compile],
                    with_features = [with_feature_set(features = ["opt"])],
                    flag_groups = [flag_group(flags = [
                        "-g0",
                        "-O2",
                        "-D_FORTIFY_SOURCE=1",
                        "-DNDEBUG",
                        "-ffunction-sections",
                        "-fdata-sections",
                    ])],
                ),
            ],
        ),

        # Linker flags in one place; lld is preferred on linux/konan
        feature(
            name = "konan_link",
            enabled = True,
            flag_sets = [
                flag_set(
                    actions = [
                        ACTION_NAMES.cpp_link_executable,
                        ACTION_NAMES.cpp_link_dynamic_library,
                        ACTION_NAMES.cpp_link_nodeps_dynamic_library,
                    ],
                    flag_groups = [flag_group(flags = [
                        "--target=" + ("x86_64-unknown-linux-gnu" if target == "linux_x64" else "aarch64-unknown-linux-gnu"),
                        "-no-canonical-prefixes",
                        "-fuse-ld=lld",
                        "-Wl,--build-id=md5",
                        "-Wl,--hash-style=gnu",
                        "-Wl,-z,relro,-z,now",
                    ])],
                ),
                # Link-time GC (opt only)
                flag_set(
                    actions = [
                        ACTION_NAMES.cpp_link_executable,
                        ACTION_NAMES.cpp_link_dynamic_library,
                        ACTION_NAMES.cpp_link_nodeps_dynamic_library,
                    ],
                    with_features = [with_feature_set(features = ["opt"])],
                    flag_groups = [flag_group(flags = ["-Wl,--gc-sections"])],
                ),
            ],
        ),

        feature(name = "opt"),
        feature(name = "dbg"),
        feature(name = "fastbuild"),
    ]

    includes = []
    if target == "linux_x64":
        includes += [
            deps + "/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/lib/gcc/x86_64-unknown-linux-gnu/8.3.0/include",
            deps + "/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/x86_64-unknown-linux-gnu/include/c++/8.3.0",
            deps + "/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/x86_64-unknown-linux-gnu/include/c++/8.3.0/x86_64-unknown-linux-gnu",
            deps + "/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/x86_64-unknown-linux-gnu/sysroot/usr/include",
        ]
    elif target == "linux_arm64":
        includes += [
            deps + "/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2/lib/gcc/aarch64-unknown-linux-gnu/8.3.0/include",
            deps + "/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2/aarch64-unknown-linux-gnu/include/c++/8.3.0",
            deps + "/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2/aarch64-unknown-linux-gnu/include/c++/8.3.0/aarch64-unknown-linux-gnu",
            deps + "/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2/aarch64-unknown-linux-gnu/sysroot/usr/include",
        ]

    return cc_common.create_cc_toolchain_config_info(
        ctx = ctx,
        toolchain_identifier = "konan_" + target,
        abi_libc_version = "glibc",
        abi_version = "glibc",
        compiler = "clang",
        host_system_name = "local",
        target_cpu = cpu,
        target_system_name = "linux",
        tool_paths = tool_paths,
        features = features,
        cxx_builtin_include_directories = [
            # Built-in include directories from the toolchain.
            # Only one will be available on the host (x64 or arm64).
            deps + "/llvm-19-aarch64-macos-essentials-79/lib/clang/19/include",
            deps + "/llvm-19-x86_64-macos-essentials-103/lib/clang/19/include",
        ] + includes,
    )

cc_toolchain_config = rule(
    implementation = _impl,
    attrs = {
        "target": attr.string(mandatory = True),
    },
    provides = [CcToolchainConfigInfo],
)
