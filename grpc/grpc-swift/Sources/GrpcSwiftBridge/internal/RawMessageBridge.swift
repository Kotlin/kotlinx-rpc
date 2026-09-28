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


private enum RawMessageBridgeError: Error {
    case negativeMessageLength(Int)
    case messageFillFailed(expectedLength: Int)
}

extension SwiftGrpcRequestSource {
    
    internal func makeStreamingClientRequest() -> StreamingClientRequest<RawMessage> {
        StreamingClientRequest<RawMessage> { writer in
            defer { self.cancel() }
            
            while let request = try await self.nextRequestMessage() {
                try Task.checkCancellation()
                
                let message = try request.makeRawMessage()
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
        initialRequest: (any SwiftGrpcRequestMessage)?
    ) -> StreamingClientRequest<RawMessage> {
        StreamingClientRequest<RawMessage> { writer in
            defer { self.cancel() }
            
            var request = initialRequest
            if request == nil {
                request = try await self.nextRequestMessage()
            }
            
            if let request {
                try Task.checkCancellation()
                
                let message = try request.makeRawMessage()
                try await writer.write(message)
            }
        }
    }
    
    private func nextRequestMessage() async throws -> SwiftGrpcRequestMessage? {
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

extension SwiftGrpcRequestMessage {

    internal func makeRawMessage() throws -> RawMessage {
        if let owned = self as? SwiftGrpcRequestBytes {
            return try owned.takeRawMessage()
        }
        return try copyToRawMessage()
    }
    
    private func copyToRawMessage() throws -> RawMessage {
        guard length >= 0 else {
            throw RawMessageBridgeError.negativeMessageLength(length)
        }
        
        var result = RawMessage(repeating: 0, count: length)
        
        let didFill = result.withUnsafeMutableBytes { buffer in
            fillBuffer(buffer.baseAddress, capacity: buffer.count)
        }
        
        guard didFill else {
            throw RawMessageBridgeError.messageFillFailed(expectedLength: length)
        }
        
        return result
    }
    
}
