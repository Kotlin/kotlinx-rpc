def _grpc_archive_outputs_impl(ctx):
    cc_info = ctx.attr.dep[CcInfo]
    files = []

    for linker_input in cc_info.linking_context.linker_inputs.to_list():
        for library in linker_input.libraries:
            # Some gRPC transitive libraries only show up as logical CcInfo link inputs when building :grpc directly.
            # Returning them as DefaultInfo outputs forces Bazel to materialize those archives so Gradle can package
            # the full archive set instead of seeing link entries that have no file on disk.
            if library.pic_static_library:
                files.append(library.pic_static_library)
            elif library.static_library:
                files.append(library.static_library)

    return [
        DefaultInfo(files = depset(files)),
        cc_info,
    ]

grpc_archive_outputs = rule(
    implementation = _grpc_archive_outputs_impl,
    attrs = {
        "dep": attr.label(providers = [CcInfo]),
    },
)
