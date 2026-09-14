/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

struct ServerTarget: Equatable, Sendable, CustomStringConvertible {
    let host: String
    let port: Int

    var description: String {
        self.host.contains(":") ? "[\(self.host)]:\(self.port)" : "\(self.host):\(self.port)"
    }

    static func parse(_ value: String) throws -> ServerTarget {
        let host: Substring
        let portText: Substring

        if value.hasPrefix("[") {
            guard let closingBracket = value.firstIndex(of: "]") else {
                throw CLIError.usage("Invalid target '\(value)'; expected [IPv6]:PORT")
            }
            host = value[value.index(after: value.startIndex)..<closingBracket]
            let colon = value.index(after: closingBracket)
            guard colon < value.endIndex, value[colon] == ":" else {
                throw CLIError.usage("Invalid target '\(value)'; expected [IPv6]:PORT")
            }
            portText = value[value.index(after: colon)...]
        } else {
            guard let colon = value.lastIndex(of: ":") else {
                throw CLIError.usage("Invalid target '\(value)'; expected HOST:PORT")
            }
            host = value[..<colon]
            portText = value[value.index(after: colon)...]
        }

        guard !host.isEmpty, let port = Int(portText), (1...65_535).contains(port) else {
            throw CLIError.usage("Invalid target '\(value)'; expected HOST:PORT with a valid port")
        }
        return ServerTarget(host: String(host), port: port)
    }
}
