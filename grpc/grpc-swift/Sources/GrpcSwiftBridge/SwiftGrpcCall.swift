import Foundation
import GRPCCore

/// A running raw-message gRPC call.
///
/// Responses are exposed as an asynchronous pull stream. Consumers must follow these rules:
///
/// - Call ``nextEvent(_:)`` to request one event, then wait for its completion handler before
///   requesting another.
/// - Only one event request may be outstanding at a time. A concurrent request is rejected without
///   affecting the outstanding request.
/// - Events arrive in the order `Headers? -> Message* -> Closed` for a successful RPC.
/// - A ``SwiftGrpcClosedEvent`` or a stream error delivered for an accepted request is terminal. Do
///   not request another event afterward.
/// - Cancelling the call is terminal. An outstanding event request completes with a cancellation
///   error rather than a closed event.
@objc(SwiftGrpcCall)
public final class SwiftGrpcCall: NSObject, @unchecked Sendable {
    private let mailbox: ResponseEventMailbox
    private let callTask: Task<Void, Never>
    private let requestSource: any SwiftGrpcRequestSource

    // Keeping the owner alive also keeps its connection task alive for the duration of the call.
    private let owner: SwiftGrpcClient

    internal init(
        owner: SwiftGrpcClient,
        descriptor: MethodDescriptor,
        options: CallOptions,
        metadata: Metadata,
        requestSource: any SwiftGrpcRequestSource,
        initialRequest: (any SwiftGrpcRequestMessage)?
    ) {
        let mailbox = ResponseEventMailbox()
        let callRunner = CallRunner(
            client: owner.client,
            descriptor: descriptor,
            options: options,
            metadata: metadata,
            requestSource: requestSource,
            initialRequest: initialRequest,
            mailbox: mailbox
        )

        self.owner = owner
        self.mailbox = mailbox
        self.requestSource = requestSource
        self.callTask = Task {
            await callRunner.run()
        }
        super.init()
    }

    /// Requests the next response event without blocking the calling thread.
    ///
    /// Implementations invoke `completion` exactly once. A successful call returns a non-null
    /// event. Bridge failures are reported through `error`; a non-OK gRPC status is represented by
    /// a ``SwiftGrpcClosedEvent`` instead.
    ///
    /// `completion` may run before this method returns, on a Swift concurrency executor, or on the
    /// thread that cancels the call.
    ///
    /// For a succeeding gRPC call it the following event order is guaranteed:
    /// `Headers? -> Message* -> Closed`
    @objc(nextEventWithCompletion:)
    public func nextEvent(
        _ completion: @escaping @Sendable (SwiftGrpcCallEvent?, NSError?) -> Void
    ) {
        self.mailbox.nextEvent(completion)
    }

    /// Cancels the RPC and any outstanding request or event pull.
    ///
    /// Swift task cancellation doesn't carry a message, so `message` is informational only.
    @objc(cancelWithMessage:)
    public func cancel(message _: String?) {
        // End the mailbox first so an outstanding event pull completes without waiting for
        // grpc-swift to unwind, and so cancellation wins over the status it reports.
        self.mailbox.cancel()
        self.callTask.cancel()
        self.requestSource.cancel()
    }

    /// Cancels the RPC and invokes `completion` after its task has stopped.
    ///
    /// The callback may run on any Swift concurrency executor.
    @objc(cancelAndWaitWithMessage:completion:)
    public func cancelAndWait(
        message: String?,
        completion: @escaping @Sendable () -> Void
    ) {
        let callTask = self.callTask
        self.cancel(message: message)
        Task {
            await callTask.value
            completion()
        }
    }

    deinit {
        self.mailbox.cancel()
        self.callTask.cancel()
        self.requestSource.cancel()
    }
}
