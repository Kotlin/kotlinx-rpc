/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

struct ServerTarget: Equatable, Sendable, CustomStringConvertible {
    let port: Int
    let host = "localhost"

    var description: String { "\(self.host):\(self.port)" }
}
