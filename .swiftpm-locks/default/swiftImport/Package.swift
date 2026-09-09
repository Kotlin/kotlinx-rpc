// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "KotlinMultiplatformLinkedPackage",
  platforms: [
    .iOS("18.0"),
    .macOS("10.15")
  ],
  products: [
    .library(
      name: "KotlinMultiplatformLinkedPackage",
      type: .none,
      targets: ["KotlinMultiplatformLinkedPackage"]
    )
  ],
  dependencies: [
    .package(path: "subpackages/_grpc_grpc-client"),
    .package(path: "subpackages/_grpc_grpc-swift"),
    .package(path: "subpackages/_grpc_grpc_swift")
  ],
  targets: [
    .target(
      name: "KotlinMultiplatformLinkedPackage",
      dependencies: [
        .product(name: "_grpc_grpc-client", package: "_grpc_grpc-client"),
        .product(name: "_grpc_grpc-swift", package: "_grpc_grpc-swift"),
        .product(name: "_grpc_grpc_swift", package: "_grpc_grpc_swift")
      ]
    )
  ]
)
