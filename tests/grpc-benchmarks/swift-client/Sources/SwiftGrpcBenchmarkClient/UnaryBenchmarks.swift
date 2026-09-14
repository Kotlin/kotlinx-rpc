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
            defaults: try! BenchmarkParameters(
                warmupCalls: 100,
                calls: 1_000,
                concurrency: 1,
                requestBytes: 0,
                responseBytes: 0
            )
        ),
        unaryBenchmark(
            name: "unary-throughput",
            description: "Warm concurrent unary call throughput",
            defaults: try! BenchmarkParameters(
                warmupCalls: 100,
                calls: 10_000,
                concurrency: 16,
                requestBytes: 1_024,
                responseBytes: 1_024
            )
        ),
    ]
}

private func unaryBenchmark(
    name: String,
    description: String,
    defaults: BenchmarkParameters
) -> any Benchmark {
    CallBenchmark(name: name, description: description, defaults: defaults) { client, parameters in
        let service = Grpc_Testing_BenchmarkService.Client(wrapping: client)
        let request = unaryRequest(
            requestBytes: parameters.requestBytes,
            responseBytes: parameters.responseBytes
        )

        return MeasuredCall(
            applicationBytes: Int64(parameters.requestBytes) + Int64(parameters.responseBytes),
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

private func unaryRequest(requestBytes: Int, responseBytes: Int) -> Grpc_Testing_SimpleRequest {
    var payload = Grpc_Testing_Payload()
    payload.type = .compressable
    payload.body = Data((0..<requestBytes).lazy.map { UInt8($0 % 251) })

    var request = Grpc_Testing_SimpleRequest()
    request.responseType = .compressable
    request.responseSize = Int32(responseBytes)
    request.payload = payload
    return request
}

private struct UnexpectedResponseSize: Error, CustomStringConvertible, Sendable {
    let expected: Int
    let actual: Int

    var description: String {
        "Expected \(self.expected) response bytes, got \(self.actual)"
    }
}
