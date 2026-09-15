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
        XCTAssertTrue(output.contains("unary-payload-sweep"))
        XCTAssertTrue(output.contains("unary-concurrency-sweep"))
        XCTAssertTrue(output.contains("symmetric-1m"))
        XCTAssertTrue(output.contains("1k-c128"))
    }

    func testRunHelpDescribesOptions() throws {
        let output = try self.cli.help(for: "run")

        XCTAssertTrue(output.contains("--target"))
        XCTAssertTrue(output.contains("--concurrency"))
        XCTAssertTrue(output.contains("--case"))
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
                    caseName: nil,
                    target: ServerTarget(host: "::1", port: 1234),
                    overrides: BenchmarkOverrides(calls: 25),
                    format: .csv
                )
            )
        )
    }

    func testParsesNamedCase() throws {
        let command = try self.cli.parse(arguments: [
            "run", "unary-payload-sweep",
            "--case", "upload-1m",
        ])

        XCTAssertEqual(
            command,
            .run(
                RunConfiguration(
                    benchmarkName: "unary-payload-sweep",
                    caseName: "upload-1m",
                    target: ServerTarget(host: "localhost", port: 50051),
                    overrides: BenchmarkOverrides(),
                    format: .human
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

    func testRejectsUnknownBenchmarkCases() {
        XCTAssertThrowsError(
            try self.cli.parse(arguments: ["run", "unary-payload-sweep", "--case", "missing"])
        )
    }

    func testRejectsCaseSelectionForAllBenchmarks() {
        XCTAssertThrowsError(try self.cli.parse(arguments: ["run", "all", "--case", "default"]))
    }
}
