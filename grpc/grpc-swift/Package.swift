// swift-tools-version: 6.3
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "grpc-swift-bridge",
    platforms: [
        .macOS(.v15),
        .iOS(.v18)
    ],
    products: [
        // Products define the executables and libraries a package produces, making them visible to other packages.
        .library(
            name: "GrpcSwiftBridge",
            targets: ["GrpcSwiftBridge"]
        ),
    ],
    dependencies: [
        .package(
            url: "https://github.com/grpc/grpc-swift-2.git",
            exact: "2.4.3"
        ),
        .package(
            url: "https://github.com/grpc/grpc-swift-nio-transport.git",
            exact: "2.4.3"
        ),
    ],
    targets: [
        // Targets are the basic building blocks of a package, defining a module or a test suite.
        // Targets can depend on other targets in this package and products from dependencies.
        .target(
            name: "GrpcSwiftBridge",
            dependencies: [
                .product(name: "GRPCCore", package: "grpc-swift-2"),
                .product(name: "GRPCNIOTransportHTTP2TransportServices", package: "grpc-swift-nio-transport")
            ]
        ),

    ],
    swiftLanguageModes: [.v6]
)
