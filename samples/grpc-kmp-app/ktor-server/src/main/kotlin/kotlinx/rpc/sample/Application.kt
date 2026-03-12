package kotlinx.rpc.sample

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.rpc.grpc.ktor.server.grpc
import kotlinx.rpc.registerService
import kotlinx.rpc.sample.messages.MessageService

fun main() {
    embeddedServer(Netty, port = 8081) {
        grpc(port = 8080) {
            services {
                registerService<MessageService> { MessageServiceImpl() }
            }
        }

        routing {
            get("/health") {
                call.respondText("gRPC Chat Server is running (Ktor)")
            }
        }
    }.start(wait = true)
}
