// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_grpc_grpc_swift",
  platforms: [
    .iOS("18.0"),
    .macOS("10.15")
  ],
  products: [
    .library(
      name: "_grpc_grpc_swift",
      type: .none,
      targets: ["_grpc_grpc_swift"]
    )
  ],
  dependencies: [
    .package(
      path: "../../../../../grpc/grpc-swift"
    )
  ],
  targets: [
    .target(
      name: "_grpc_grpc_swift",
      dependencies: [
        .product(
          name: "GrpcSwiftBridge",
          package: "grpc-swift",
          condition: .when(platforms: [.iOS])
        )
      ]
    )
  ]
)
