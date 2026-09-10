import GRPCCore
import GRPCNIOTransportHTTP2TransportServices


typealias RawMessage = GRPCNIOTransportBytes

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
                
                let message = try request.copyToRawMessage()
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
    
    internal func copyToRawMessage() throws -> RawMessage {
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
