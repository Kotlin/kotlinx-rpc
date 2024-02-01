package com.example.ktorapplication.server
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.WebSockets
import org.jetbrains.krpc.serialization.json
import org.jetbrains.krpc.transport.ktor.server.rpc

fun Application.appRouting() {
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