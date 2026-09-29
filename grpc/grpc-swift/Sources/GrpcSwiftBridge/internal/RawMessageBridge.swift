import GRPCCore
import GRPCNIOTransportHTTP2TransportServices


internal typealias RawMessage = GRPCNIOTransportBytes

internal struct RawMessageSerializer: MessageSerializer<RawMessage> {
    func serialize<Bytes: GRPCContiguousBytes>(
        _ message: RawMessage
    ) throws -> Bytes {
        message as! Bytes
    }
}

internal struct RawMessageDeserializer: MessageDeserializer<RawMessage> {
    func deserialize<Bytes: GRPCContiguousBytes>(
        _ bytes: Bytes
    ) throws -> RawMessage {
        bytes as! RawMessage
    }
}


extension SwiftGrpcRequestSource {
    
    internal func makeStreamingClientRequest() -> StreamingClientRequest<RawMessage> {
        StreamingClientRequest<RawMessage> { writer in
            defer { self.cancel() }
            
            while let request = try await self.nextRequestMessage() {
                try Task.checkCancellation()
                
                let message = try request.takeRawMessage()
                try await writer.write(message)
            }
        }
    }
    
    /// Sends the only request of a unary or server-streaming call.
    ///
    /// An `initialRequest` is sent without pulling. Otherwise, the source completes its only pull
    /// after the request flow ended with exactly one request, so no end-of-stream pull is needed.
    /// `nil` means that the call was cancelled first.
    internal func makeSingleClientRequest(
        initialRequest: SwiftGrpcRequestBytes?
    ) -> StreamingClientRequest<RawMessage> {
        StreamingClientRequest<RawMessage> { writer in
            defer { self.cancel() }
            
            var request = initialRequest
            if request == nil {
                request = try await self.nextRequestMessage()
            }
            
            if let request {
                try Task.checkCancellation()
                
                let message = try request.takeRawMessage()
                try await writer.write(message)
            }
        }
    }
    
    private func nextRequestMessage() async throws -> SwiftGrpcRequestBytes? {
        try await awaitSwiftGrpcCompletion(onCancellation: {
            // This also completes an outstanding Kotlin callback, allowing
            // the checked continuation to resume.
            self.cancel()
        }) { completion in
            self.nextRequest { message, error in
                completion(
                    message,
                    error
                )
            }
        }
    }
    
}
