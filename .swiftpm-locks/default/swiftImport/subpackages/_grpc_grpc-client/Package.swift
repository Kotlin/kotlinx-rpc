// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_grpc_grpc-client",
  platforms: [
    .iOS("18.0"),
    .macOS("10.15")
  ],
  products: [
    .library(
      name: "_grpc_grpc-client",
      type: .none,
      targets: ["_grpc_grpc-client"]
    )
  ],
  dependencies: [
  ],
  targets: [
    .target(
      name: "_grpc_grpc-client",
      dependencies: [
      ]
    )
  ]
)
