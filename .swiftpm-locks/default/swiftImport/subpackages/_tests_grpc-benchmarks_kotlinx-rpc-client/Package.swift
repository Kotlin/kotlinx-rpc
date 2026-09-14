// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_tests_grpc-benchmarks_kotlinx-rpc-client",
  platforms: [
    .iOS("18.0"),
    .macOS("10.15")
  ],
  products: [
    .library(
      name: "_tests_grpc-benchmarks_kotlinx-rpc-client",
      type: .none,
      targets: ["_tests_grpc-benchmarks_kotlinx-rpc-client"]
    )
  ],
  dependencies: [
  ],
  targets: [
    .target(
      name: "_tests_grpc-benchmarks_kotlinx-rpc-client",
      dependencies: [
      ]
    )
  ]
)
