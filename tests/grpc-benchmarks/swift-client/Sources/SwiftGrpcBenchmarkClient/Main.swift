/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import Darwin
import Foundation

@main
enum SwiftGrpcBenchmarkClient {
    static func main() async {
        do {
            try await BenchmarkCLI().execute(arguments: Array(CommandLine.arguments.dropFirst()))
        } catch let error as CLIError {
            writeError(error.description)
            exit(error.exitCode)
        } catch {
            writeError(String(describing: error))
            exit(1)
        }
    }

    private static func writeError(_ message: String) {
        FileHandle.standardError.write(Data("\(message)\n".utf8))
    }
}
