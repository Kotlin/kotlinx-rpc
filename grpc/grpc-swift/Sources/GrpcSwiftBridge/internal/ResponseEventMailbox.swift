import Foundation
import Synchronization

/// Hands response events from a ``CallRunner`` to Kotlin event pulls, one at a time.
///
/// The mailbox replaces a rendezvous channel plus one Swift task per pull. It holds at most one
/// accepted pull callback or one undelivered event, never both:
///
/// - `idle`: Neither a pull nor an event is waiting.
/// - `waitingPull`: Kotlin requested an event before the producer had one.
/// - `offered`: The producer has an event, but no pull is waiting. The producer is suspended
///   until a pull claims the event, so it cannot advance to a second event.
/// - `terminalPending`: The call failed or was cancelled before a pull could observe it. The next
///   pull receives the error once.
/// - `terminalDelivered`: A terminal result was delivered. Every later pull is rejected.
///
/// State changes happen under one lock. Callbacks and continuation resumptions happen after the
/// lock is released, so a callback may synchronously request another event or cancel the call.
///
/// Only ``cancel()`` and ``fail(_:)`` end the mailbox. Cancellation of the producer task alone
/// only withdraws its pending offer: grpc-swift cancels the response handler's task when a
/// deadline expires, and the runner must still publish the resulting status as a closed event.
internal final class ResponseEventMailbox: Sendable {
    internal typealias Completion = @Sendable (SwiftGrpcCallEvent?, NSError?) -> Void

    /// A snapshot of the current state, for tests.
    internal enum Phase: Equatable, Sendable {
        case idle
        case waitingPull
        case offered
        case terminalPending
        case terminalDelivered
    }

    private enum State {
        case idle
        case waitingPull(Completion)
        case offered(SwiftGrpcCallEvent, UnsafeContinuation<Void, any Error>)
        case terminalPending(NSError)
        case terminalDelivered
    }

    private enum PullAction {
        case wait
        case reject
        case deliver(SwiftGrpcCallEvent, UnsafeContinuation<Void, any Error>)
        case fail(NSError)
    }

    private enum SendAction {
        case deliver(Completion)
        case offer
        case reject
    }

    private enum OfferAction {
        case deliver(Completion)
        case stored
        case reject
    }

    private enum TerminateAction {
        case none
        case fail(Completion)
        case release(UnsafeContinuation<Void, any Error>)
    }

    private let state = Mutex<State>(.idle)

    internal init() {}

    internal var phase: Phase {
        self.state.withLock { state in
            switch state {
            case .idle: .idle
            case .waitingPull: .waitingPull
            case .offered: .offered
            case .terminalPending: .terminalPending
            case .terminalDelivered: .terminalDelivered
            }
        }
    }

    /// Requests the next event. `completion` is invoked exactly once, possibly before this method
    /// returns.
    ///
    /// A concurrent pull, or a pull after a delivered terminal result, is rejected with
    /// ``CallBridgeError/invalidEventPull`` without affecting the accepted pull.
    internal func nextEvent(_ completion: @escaping Completion) {
        let action = self.state.withLock { state -> PullAction in
            switch state {
            case .idle:
                state = .waitingPull(completion)
                return .wait

            case .waitingPull, .terminalDelivered:
                return .reject

            case .offered(let event, let ack):
                state = event.isTerminal ? .terminalDelivered : .idle
                return .deliver(event, ack)

            case .terminalPending(let error):
                state = .terminalDelivered
                return .fail(error)
            }
        }

        switch action {
        case .wait:
            break

        case .reject:
            completion(nil, CallBridgeError.invalidEventPull as NSError)

        case .deliver(let event, let ack):
            // Resume the producer only after the callback returns. A pull the callback makes
            // synchronously registers first, but is served from the producer's stack, not this one.
            completion(event, nil)
            ack.resume()

        case .fail(let error):
            completion(nil, error)
        }
    }

    /// Delivers `event` to a waiting pull, or waits until a pull claims it.
    ///
    /// Throws `CancellationError` if the mailbox has ended or the calling task is cancelled. In
    /// the latter case, an unclaimed event is withdrawn and the mailbox stays open.
    internal func send(_ event: SwiftGrpcCallEvent) async throws {
        try Task.checkCancellation()

        // A waiting pull is served without creating a continuation.
        let action = self.state.withLock { state -> SendAction in
            switch state {
            case .waitingPull(let completion):
                state = event.isTerminal ? .terminalDelivered : .idle
                return .deliver(completion)

            case .idle:
                return .offer

            case .offered:
                preconditionFailure("The response producer made a second offer before the first was claimed")

            case .terminalPending, .terminalDelivered:
                return .reject
            }
        }

        switch action {
        case .deliver(let completion):
            completion(event, nil)

        case .reject:
            throw CancellationError()

        case .offer:
            try await self.offer(event)
        }

        try Task.checkCancellation()
    }

    private func offer(_ event: SwiftGrpcCallEvent) async throws {
        try await withTaskCancellationHandler {
            try await withUnsafeThrowingContinuation { (ack: UnsafeContinuation<Void, any Error>) in
                // A pull or cancellation may have arrived since the first check.
                let action = self.state.withLock { state -> OfferAction in
                    switch state {
                    case .waitingPull(let completion):
                        state = event.isTerminal ? .terminalDelivered : .idle
                        return .deliver(completion)

                    case .idle:
                        // The cancellation handler may already have run and found nothing to
                        // withdraw. The task's cancelled flag is set before its handlers run.
                        if Task.isCancelled {
                            return .reject
                        }
                        state = .offered(event, ack)
                        return .stored

                    case .offered:
                        preconditionFailure("The response producer made a second offer before the first was claimed")

                    case .terminalPending, .terminalDelivered:
                        return .reject
                    }
                }

                switch action {
                case .deliver(let completion):
                    completion(event, nil)
                    ack.resume()

                case .stored:
                    break

                case .reject:
                    ack.resume(throwing: CancellationError())
                }
            }
        } onCancel: {
            self.withdraw()
        }
    }

    /// Returns an unclaimed offered event to its producer by throwing `CancellationError` from its
    /// ``send(_:)``. The mailbox stays open, so the producer can still publish a terminal event.
    internal func withdraw() {
        let ack = self.state.withLock { state -> UnsafeContinuation<Void, any Error>? in
            guard case .offered(_, let ack) = state else {
                return nil
            }
            state = .idle
            return ack
        }
        ack?.resume(throwing: CancellationError())
    }

    /// Ends the mailbox with a cancellation error.
    internal func cancel() {
        self.fail(CancellationError() as NSError)
    }

    /// Ends the mailbox with `error`, unless it has already ended.
    ///
    /// A waiting pull receives `error` immediately. Otherwise, the next pull receives it. An
    /// unclaimed event is dropped and its producer's ``send(_:)`` throws.
    internal func fail(_ error: NSError) {
        let action = self.state.withLock { state -> TerminateAction in
            switch state {
            case .idle:
                state = .terminalPending(error)
                return .none

            case .waitingPull(let completion):
                state = .terminalDelivered
                return .fail(completion)

            case .offered(_, let ack):
                state = .terminalPending(error)
                return .release(ack)

            case .terminalPending, .terminalDelivered:
                // The first terminal result wins.
                return .none
            }
        }

        switch action {
        case .none:
            break

        case .fail(let completion):
            completion(nil, error)

        case .release(let ack):
            ack.resume(throwing: CancellationError())
        }
    }
}

private extension SwiftGrpcCallEvent {
    var isTerminal: Bool {
        self is SwiftGrpcClosedEvent
    }
}
