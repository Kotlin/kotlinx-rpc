/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */
package org.jetbrains.ktorapplication

import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import org.jetbrains.krpc.serialization.json
import org.jetbrains.krpc.transport.ktor.server.rpc

fun main(args: Array<String>): Unit = EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    install(WebSockets)

    routing {
        rpc("/api") {
            rpcConfig {
                serialization {
                    json()
                }
            }

            registerService<MyService>(MyServiceImpl())
        }
    }
}