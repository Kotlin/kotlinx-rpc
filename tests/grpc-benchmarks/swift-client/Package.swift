// swift-tools-version: 6.1

/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import PackageDescription

let package = Package(
    name: "swift-grpc-benchmark-client",
    platforms: [
        .macOS(.v15),
        .iOS(.v18),
    ],
    products: [
        .executable(
            name: "swift-grpc-benchmark-client",
            targets: ["SwiftGrpcBenchmarkClient"]
        ),
    ],
    dependencies: [
        .package(url: "https://github.com/grpc/grpc-swift-2.git", exact: "2.4.3"),
        .package(url: "https://github.com/grpc/grpc-swift-nio-transport.git", exact: "2.9.2"),
        .package(url: "https://github.com/grpc/grpc-swift-protobuf.git", exact: "2.4.0"),
    ],
    targets: [
        .executableTarget(
            name: "SwiftGrpcBenchmarkClient",
            dependencies: [
                .product(name: "GRPCCore", package: "grpc-swift-2"),
                .product(
                    name: "GRPCNIOTransportHTTP2TransportServices",
                    package: "grpc-swift-nio-transport"
                ),
                .product(name: "GRPCProtobuf", package: "grpc-swift-protobuf"),
            ],
            plugins: [
                .plugin(name: "GRPCProtobufGenerator", package: "grpc-swift-protobuf"),
            ]
        ),
        .testTarget(
            name: "SwiftGrpcBenchmarkClientTests",
            dependencies: ["SwiftGrpcBenchmarkClient"]
        ),
    ],
    swiftLanguageModes: [.v6]
)
