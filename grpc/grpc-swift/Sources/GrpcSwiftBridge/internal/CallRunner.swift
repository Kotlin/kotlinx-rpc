import Foundation
import GRPCCore

/// Executes one grpc-swift call and produces the bridge's ordered response event stream.
internal struct CallRunner: Sendable {
    private let client: SwiftGrpcClient.Client
    private let descriptor: MethodDescriptor
    private let options: CallOptions
    private let metadata: Metadata
    private let requestSource: any SwiftGrpcRequestSource
    private let mailbox: ResponseEventMailbox

    internal init(
        client: SwiftGrpcClient.Client,
        descriptor: MethodDescriptor,
        options: CallOptions,
        metadata: Metadata,
        requestSource: any SwiftGrpcRequestSource,
        mailbox: ResponseEventMailbox
    ) {
        self.client = client
        self.descriptor = descriptor
        self.options = options
        self.metadata = metadata
        self.requestSource = requestSource
        self.mailbox = mailbox
    }

    internal func run() async {
        // A response may terminate before grpc-swift consumes the complete Kotlin request flow.
        // Cancelling the source also resumes any request pull still waiting on Kotlin.
        defer { self.requestSource.cancel() }

        // Turn the Kotlin request source into a grpc-swift-compatible client request.
        var request = self.requestSource.makeStreamingClientRequest()
        request.metadata = self.metadata

        do {
            try await self.client.bidirectionalStreaming(
                request: request,
                descriptor: self.descriptor,
                serializer: RawMessageSerializer(),
                deserializer: RawMessageDeserializer(),
                options: self.options
            ) { response in
                // The handler may run in a child task that grpc-swift cancels when the deadline
                // expires. Its cancellation only withdraws a pending event; the resulting
                // RPCError is published below as the closed event.
                switch response.accepted {
                case .success(let contents):
                    // Sends suspend until Kotlin requests the event.
                    // This preserves ordering and propagates backpressure into grpc-swift.
                    try await self.mailbox.send(
                        SwiftGrpcHeadersEvent(headers: SwiftGrpcMetadata(contents.metadata))
                    )

                    for try await part in contents.bodyParts {
                        switch part {
                        case .message(let message):
                            try await self.mailbox.send(SwiftGrpcMessageEvent(message: message))

                        case .trailingMetadata(let trailers):
                            // Successful trailers become the single terminal event.
                            try await self.mailbox.send(
                                SwiftGrpcClosedEvent(
                                    code: .ok,
                                    message: nil,
                                    trailers: trailers
                                )
                            )
                            return
                        }
                    }

                    // An accepted grpc-swift response must end in status/trailing metadata.
                    throw CallBridgeError.missingTerminalEvent

                case .failure(let error):
                    // Rejection is a normal gRPC result without a body, not a bridge failure.
                    try await self.mailbox.send(error.closedEvent)
                }
            }
        } catch let error as RPCError {
            // A non-OK status encountered while consuming the body is also a closed event. Local
            // cancellation takes precedence because the caller explicitly abandoned the RPC.
            // This task is only cancelled by the caller, which has already ended the mailbox.
            if Task.isCancelled {
                self.mailbox.cancel()
            } else {
                do {
                    try await self.mailbox.send(error.closedEvent)
                } catch {
                    self.mailbox.cancel()
                }
            }
        } catch is CancellationError {
            self.mailbox.cancel()
        } catch {
            if Task.isCancelled {
                self.mailbox.cancel()
            } else {
                self.mailbox.fail(error as NSError)
            }
        }

        // Every path above publishes a terminal result; this is a no-op unless one was missed.
        self.mailbox.fail(CallBridgeError.missingTerminalEvent as NSError)
    }
}

internal enum CallBridgeError: LocalizedError, Sendable {
    case invalidEventPull
    case missingTerminalEvent

    internal var errorDescription: String? {
        switch self {
        case .invalidEventPull:
            "Only one event pull may be outstanding, and no pulls are allowed after close"
        case .missingTerminalEvent:
            "The gRPC response stream ended without a terminal event"
        }
    }
}

private extension RPCError {
    var closedEvent: SwiftGrpcClosedEvent {
        SwiftGrpcClosedEvent(
            code: SwiftGrpcStatusCode(rawValue: self.code.rawValue) ?? .unknown,
            message: self.message,
            trailers: self.metadata
        )
    }
}

private extension SwiftGrpcClosedEvent {
    convenience init(
        code: SwiftGrpcStatusCode,
        message: String?,
        trailers: Metadata
    ) {
        self.init(
            status: SwiftGrpcStatus(code: code, message: message),
            trailers: SwiftGrpcMetadata(trailers)
        )
    }
}
