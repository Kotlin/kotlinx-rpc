// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_grpc_grpc-core",
  platforms: [
    .iOS("18.0"),
    .macOS("10.15")
  ],
  products: [
    .library(
      name: "_grpc_grpc-core",
      type: .none,
      targets: ["_grpc_grpc-core"]
    )
  ],
  dependencies: [
  ],
  targets: [
    .target(
      name: "_grpc_grpc-core",
      dependencies: [
      ]
    )
  ]
)
