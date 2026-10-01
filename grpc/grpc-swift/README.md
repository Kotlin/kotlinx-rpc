# gRPC-Swift Bridge

This module supplies the grpc-swift-2 backend for kotlinx-rpc iOS clients. It uses the HTTP/2
SwiftNIO Transport Services transport backed by Apple's Network.framework. The bridge APIs exposed
to Kotlin are annotated with `@objc` and imported through Kotlin Gradle plugin SwiftPM support.

The backend is selected automatically by `grpc-client`'s `iosMain` source set. Applications keep
using `GrpcClient`, generated Kotlin service interfaces, and Kotlin protobuf messages. macOS and
Linux clients continue to use gRPC Core; this package does not supply a server backend.

## Requirements and dependencies

- Kotlin 2.4.20, as configured in the root version catalog, with SwiftPM import support.
- Xcode with Swift tools 6.3 or newer, as required by `Package.swift`.
- iOS 18.0 or newer for the consuming app. Published Kotlin targets are `iosArm64`,
  `iosSimulatorArm64`, and `iosX64`.
- grpc-swift-2 2.4.3 and grpc-swift-nio-transport 2.9.2, pinned in `Package.swift`.
  SwiftNIO is a transitive dependency; resolved versions are recorded in `Package.resolved`.

The package also declares macOS 15 for Swift-side development and tests. This does not switch
the kotlinx-rpc macOS client to the Swift backend.

The Gradle module publishes the bridge's SwiftPM metadata and sources. `grpc-client` depends on it
from `iosMain`, so applications do not need to add the bridge or its Swift packages separately.
The module configures SwiftPM import builds to use the Release Xcode configuration; see
`configureSwiftPMXcodeBuildConfiguration` in `gradle-conventions` for the KT-83900 workaround.

## Bridge contracts

- `SwiftGrpcClient` owns a long-lived grpc-swift client and its connection task. Graceful shutdown
  lets active calls finish; forceful shutdown cancels them. Termination callbacks report when the
  connection task has stopped.
- Kotlin request flows feed a pull-based `SwiftGrpcRequestSource`. Streaming calls allow one
  outstanding pull; unary and server-streaming calls validate exactly one request before sending.
- `SwiftGrpcCall` exposes response events in `Headers? -> Message* -> Closed` order. Only one event
  pull may be outstanding. `ResponseEventMailbox` suspends the producer until an offered event is
  consumed, keeping response delivery bounded.
- Completion handlers may run synchronously or on Swift concurrency executors. Kotlin coroutine
  cancellation cancels the Swift call; cleanup waits for the call task and request collector.
- Non-OK gRPC statuses are closed events. Objective-C completion-handler errors represent bridge
  failures and become `SwiftGrpcInteropException` on the Kotlin side.
- `SwiftGrpcRequestBytes` owns exact-size request storage, which Kotlin writes and seals before
  grpc-swift takes it once. Response buffers are borrowed only within `withUnsafeBytes` and decoded
  synchronously. Pointers must never escape their callbacks. Binary metadata is copied into Kotlin
  byte arrays, preserving duplicate entries.

Detailed ownership, cancellation, and callback rules live alongside the Swift declarations and
the Kotlin adapters in `grpc-client/src/iosMain`.


