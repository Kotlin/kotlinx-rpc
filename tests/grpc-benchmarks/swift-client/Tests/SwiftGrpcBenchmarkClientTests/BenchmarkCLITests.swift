/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import XCTest
@testable import SwiftGrpcBenchmarkClient

final class BenchmarkCLITests: XCTestCase {
    private let cli = BenchmarkCLI()

    func testListsBenchmarks() {
        let output = self.cli.listBenchmarks()

        XCTAssertTrue(output.contains("unary-latency"))
        XCTAssertTrue(output.contains("unary-throughput"))
    }

    func testRunHelpDescribesOptions() throws {
        let output = try self.cli.help(for: "run")

        XCTAssertTrue(output.contains("--target"))
        XCTAssertTrue(output.contains("--concurrency"))
        XCTAssertTrue(output.contains("--request-bytes"))
        XCTAssertTrue(output.contains("--format"))
    }

    func testParsesRunOverrides() throws {
        let command = try self.cli.parse(arguments: [
            "run", "unary-throughput",
            "--target", "[::1]:1234",
            "--calls", "25",
            "--format", "csv",
        ])

        XCTAssertEqual(
            command,
            .run(
                RunConfiguration(
                    benchmarkName: "unary-throughput",
                    target: ServerTarget(host: "::1", port: 1234),
                    overrides: BenchmarkOverrides(calls: 25),
                    format: .csv
                )
            )
        )
    }

    func testRejectsInvalidNumbers() {
        XCTAssertThrowsError(try self.cli.parse(arguments: ["run", "unary-latency", "--calls", "many"]))
        XCTAssertThrowsError(try self.cli.parse(arguments: ["run", "unary-latency", "--calls", "0"]))
    }

    func testRejectsUnknownBenchmarks() {
        XCTAssertThrowsError(try self.cli.parse(arguments: ["run", "unknown"]))
    }
}
