package com.jetbrains.krpc.samples

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import org.jetbrains.krpc.transport.ktor.server.rpc

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(WebSockets)
        routing {
            rpc<ImageRecognizer>("image-recognizer", ImageRecognizerService())
        }
    }.start(wait = true)
}