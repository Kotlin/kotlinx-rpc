GRPC_VERSION = "1.74.1"

def grpc_url(arch):
    return (
        f"https://jetbrains.team/p/krpc/packages/files/"
        f"bazel-build-deps/com_github_grpc_grpc/v{GRPC_VERSION}/libgrpc_fat.{arch}.a"
    )