package kotlinx.rpc.sample

import kotlinx.coroutines.runBlocking
import kotlinx.rpc.grpc.server.GrpcServer
import kotlinx.rpc.registerService
import kotlinx.rpc.sample.messages.MessageService

fun main(): Unit = runBlocking {
    val grpcServer = GrpcServer(8080) {
        services {
            registerService<MessageService> { MessageServiceImpl() }
        }
    }

    grpcServer.start()
    println("Server listening on port ${grpcServer.port}")
    grpcServer.awaitTermination()
}
