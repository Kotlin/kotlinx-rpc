import kotlinx.coroutines.runBlocking
import kotlinx.rpc.grpc.GrpcServer
import kotlinx.rpc.registerService

fun main(): Unit = runBlocking {
    val grpcServer = GrpcServer(8080) {
        registerService<ImageRecognizer> { ImageRecognizerImpl() }
    }

    grpcServer.start()
    grpcServer.awaitTermination()
}
