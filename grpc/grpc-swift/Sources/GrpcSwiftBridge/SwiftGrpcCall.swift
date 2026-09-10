import AsyncAlgorithms
import Foundation
import GRPCCore
import Synchronization

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
    private let eventChannel: AsyncThrowingChannel<SwiftGrpcCallEvent, Error>
    private let pullState: PullState
    private let callTask: Task<Void, Never>
    private let requestSource: any SwiftGrpcRequestSource

    // Keeping the owner alive also keeps its connection task alive for the duration of the call.
    private let owner: SwiftGrpcClient

    internal init(
        owner: SwiftGrpcClient,
        descriptor: MethodDescriptor,
        options: CallOptions,
        metadata: Metadata,
        requestSource: any SwiftGrpcRequestSource
    ) {
        let eventChannel = AsyncThrowingChannel<SwiftGrpcCallEvent, Error>()
        let pullState = PullState()
        let callRunner = CallRunner(
            client: owner.client,
            descriptor: descriptor,
            options: options,
            metadata: metadata,
            requestSource: requestSource,
            eventChannel: eventChannel
        )

        self.owner = owner
        self.eventChannel = eventChannel
        self.pullState = pullState
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
    /// For a succeeding gRPC call it the following event order is guaranteed:
    /// `Headers? -> Message* -> Closed`
    @objc(nextEventWithCompletion:)
    public func nextEvent(
        _ completion: @escaping @Sendable (SwiftGrpcCallEvent?, NSError?) -> Void
    ) {
        guard self.pullState.beginPull() else {
            completion(nil, CallBridgeError.invalidEventPull as NSError)
            return
        }

        let eventChannel = self.eventChannel
        let pullState = self.pullState
        Task {
            do {
                // Using the async event channel gives us natural backpressure,
                // as the call runner can only continue with the next message
                // once we called next here.
                var iterator = eventChannel.makeAsyncIterator()
                guard let event = try await iterator.next() else {
                    // If no more element available, this means there was an internal
                    // error as we did not receive a closing event before.
                    pullState.finishPull(terminal: true)
                    completion(nil, CallBridgeError.missingTerminalEvent as NSError)
                    return
                }

                // Finish the pull and allow consumer to pull the next event, if
                // this was not a closing event.
                pullState.finishPull(terminal: event is SwiftGrpcClosedEvent)
                completion(event, nil)
            } catch {
                pullState.finishPull(terminal: true)
                completion(nil, error as NSError)
            }
        }
    }

    /// Cancels the RPC and any outstanding request or event pull.
    ///
    /// Swift task cancellation doesn't carry a message, so `message` is informational only.
    @objc(cancelWithMessage:)
    public func cancel(message _: String?) {
        self.callTask.cancel()
        self.requestSource.cancel()
    }

    deinit {
        self.callTask.cancel()
        self.requestSource.cancel()
        self.eventChannel.finish()
    }
}

private extension SwiftGrpcCall {
    /// Coordinates event requests and remembers when the response stream has terminated.
    ///
    /// The state machine has three states:
    ///
    /// - `idle`: A request may begin, transitioning the state to `pulling`.
    /// - `pulling`: One request is outstanding, so additional requests are rejected. Completing
    ///   with a non-terminal event returns the state to `idle`; completing with a terminal event or
    ///   error transitions it to `terminal`.
    /// - `terminal`: A terminal event or error has been delivered and all subsequent requests are
    ///   rejected.
    ///
    /// The state is atomic so checking whether a request is allowed and claiming it are one
    /// indivisible transition, even when callers invoke `nextEvent` concurrently.
    final class PullState: Sendable {
        private enum State: UInt8, AtomicRepresentable {
            case idle
            case pulling
            case terminal
        }

        private let state = Atomic<State>(.idle)

        func beginPull() -> Bool {
            self.state.compareExchange(
                expected: .idle,
                desired: .pulling,
                ordering: .acquiringAndReleasing
            ).exchanged
        }

        func finishPull(terminal: Bool) {
            self.state.store(terminal ? .terminal : .idle, ordering: .releasing)
        }
    }
}
