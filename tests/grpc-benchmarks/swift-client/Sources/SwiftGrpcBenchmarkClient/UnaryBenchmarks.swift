/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import Foundation
import GRPCCore
import GRPCProtobuf

func unaryBenchmarks() -> [any Benchmark] {
    [
        unaryBenchmark(
            name: "unary-latency",
            description: "Warm sequential unary call latency",
            cases: [
                benchmarkCase(
                    warmupOperations: 200,
                    operations: 10_000,
                    concurrency: 1,
                    requestBytes: 0,
                    responseBytes: 0
                )
            ]
        ),
        unaryBenchmark(
            name: "unary-throughput",
            description: "Warm concurrent unary call throughput",
            cases: [
                benchmarkCase(
                    warmupOperations: 200,
                    operations: 100_000,
                    concurrency: 16,
                    requestBytes: 1_024,
                    responseBytes: 1_024
                )
            ]
        ),
        unaryBenchmark(
            name: "unary-payload-sweep",
            description: "Unary request and response payload size sweep",
            cases: payloadSweepCases()
        ),
        unaryBenchmark(
            name: "unary-concurrency-sweep",
            description: "Unary concurrency sweep with empty and 1 KiB payloads",
            cases: concurrencySweepCases()
        ),
    ]
}

private func unaryBenchmark(
    name: String,
    description: String,
    cases: [BenchmarkCase]
) -> any Benchmark {
    CallBenchmark(name: name, description: description, cases: cases) { client, parameters in
        let service = Grpc_Testing_BenchmarkService.Client(wrapping: client)
        let request = unaryRequest(
            requestBytes: parameters.requestBytes,
            responseBytes: parameters.responseBytes
        )

        return MeasuredCall(
            applicationRequestBytes: Int64(parameters.requestBytes),
            applicationResponseBytes: Int64(parameters.responseBytes),
            execute: {
                let response = try await service.unaryCall(request)
                guard response.payload.body.count == parameters.responseBytes else {
                    throw UnexpectedResponseSize(
                        expected: parameters.responseBytes,
                        actual: response.payload.body.count
                    )
                }
            }
        )
    }
}

private struct PayloadSweepSize {
    let name: String
    let bytes: Int
    let warmupOperations: Int
    let operations: Int
}

private let payloadSweepSizes = [
    PayloadSweepSize(name: "64b", bytes: 64, warmupOperations: 200, operations: 10_000),
    PayloadSweepSize(name: "1k", bytes: 1_024, warmupOperations: 200, operations: 10_000),
    PayloadSweepSize(name: "64k", bytes: 64 * 1_024, warmupOperations: 50, operations: 3_000),
    PayloadSweepSize(name: "1m", bytes: 1_024 * 1_024, warmupOperations: 10, operations: 600),
    PayloadSweepSize(
        name: "near-4m",
        bytes: 4 * 1_024 * 1_024 - 1_024,
        warmupOperations: 5,
        operations: 150
    ),
]

private func payloadSweepCases() -> [BenchmarkCase] {
    let symmetric = payloadSweepSizes.map { size in
        payloadCase(
            name: "symmetric-\(size.name)",
            size: size,
            requestBytes: size.bytes,
            responseBytes: size.bytes
        )
    }
    let upload = payloadSweepSizes.dropFirst().map { size in
        payloadCase(
            name: "upload-\(size.name)",
            size: size,
            requestBytes: size.bytes,
            responseBytes: payloadSweepAnchorBytes
        )
    }
    let download = payloadSweepSizes.dropFirst().map { size in
        payloadCase(
            name: "download-\(size.name)",
            size: size,
            requestBytes: payloadSweepAnchorBytes,
            responseBytes: size.bytes
        )
    }
    return symmetric + upload + download
}

private func payloadCase(
    name: String,
    size: PayloadSweepSize,
    requestBytes: Int,
    responseBytes: Int
) -> BenchmarkCase {
    benchmarkCase(
        name: name,
        warmupOperations: size.warmupOperations,
        operations: size.operations,
        concurrency: 1,
        requestBytes: requestBytes,
        responseBytes: responseBytes
    )
}

private func concurrencySweepCases() -> [BenchmarkCase] {
    [("empty", 0), ("1k", 1_024)].flatMap { payloadName, payloadBytes in
        [1, 2, 4, 8, 16, 32, 64, 128].map { concurrency in
            benchmarkCase(
                name: "\(payloadName)-c\(concurrency)",
                warmupOperations: max(200, concurrency * 20),
                operations: max(20_000, concurrency * 2_000),
                concurrency: concurrency,
                requestBytes: payloadBytes,
                responseBytes: payloadBytes
            )
        }
    }
}

func benchmarkCase(
    name: String = "default",
    warmupOperations: Int,
    operations: Int,
    concurrency: Int,
    requestBytes: Int,
    responseBytes: Int
) -> BenchmarkCase {
    BenchmarkCase(
        name: name,
        parameters: try! BenchmarkParameters(
            warmupOperations: warmupOperations,
            operations: operations,
            concurrency: concurrency,
            requestBytes: requestBytes,
            responseBytes: responseBytes
        )
    )
}

private let payloadSweepAnchorBytes = 64

func unaryRequest(requestBytes: Int, responseBytes: Int) -> Grpc_Testing_SimpleRequest {
    var payload = Grpc_Testing_Payload()
    payload.type = .compressable
    payload.body = Data((0..<requestBytes).lazy.map { UInt8($0 % 251) })

    var request = Grpc_Testing_SimpleRequest()
    request.responseType = .compressable
    request.responseSize = Int32(responseBytes)
    request.payload = payload
    return request
}

struct UnexpectedResponseSize: Error, CustomStringConvertible, Sendable {
    let expected: Int
    let actual: Int

    var description: String {
        "Expected \(self.expected) response bytes, got \(self.actual)"
    }
}
