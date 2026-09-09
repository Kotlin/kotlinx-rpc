import Foundation

/// A running raw-message gRPC call.
///
/// Responses are exposed as an asynchronous pull stream. Only one `nextEvent` request may be
/// outstanding at a time. A closed event is emitted exactly once and is terminal.
@objc(SwiftGrpcCall)
public final class SwiftGrpcCall: NSObject, @unchecked Sendable {
    internal override init() {
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
        fatalError("Not yet implemented")
    }

    /// Cancels the RPC and any outstanding request or event pull.
    @objc(cancelWithMessage:)
    public func cancel(message: String?) {
        fatalError("Not yet implemented")
    }
}
