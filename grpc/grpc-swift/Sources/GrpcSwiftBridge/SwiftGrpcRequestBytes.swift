import Foundation

/// Fixed-size request bytes owned by Swift until the transport takes them.
/// Access is single-owner: callers must not use this object concurrently.
@objc(SwiftGrpcRequestBytes)
public final class SwiftGrpcRequestBytes: NSObject, @unchecked Sendable {
    private enum State {
        case writable(RawMessage)
        case writing
        case sealed(RawMessage)
        case taken
        case discarded
    }

    private enum TakeError: Error {
        case notSealed
    }

    private var state: State

    @objc public let length: Int

    /// Whether the request storage was released without being sent.
    @objc public var isDiscarded: Bool {
        if case .discarded = state { return true }
        return false
    }

    @objc public init(count: Int) {
        precondition(count >= 0, "Request length must be nonnegative")
        self.length = count
        self.state = .writable(RawMessage(repeating: 0, count: count))
        super.init()
    }

    /// Borrows the storage for one synchronous write. The pointer is valid only inside `body`.
    @objc(withWritableBytes:)
    public func withWritableBytes(_ body: (UnsafeMutableRawPointer?, Int) -> Bool) -> Bool {
        guard case .writable(var bytes) = state else { return false }
        state = .writing

        let didWrite = bytes.withUnsafeMutableBytes { buffer in
            body(buffer.isEmpty ? nil : buffer.baseAddress, buffer.count)
        }

        guard case .writing = state else { return false }
        state = didWrite ? .writable(bytes) : .discarded
        return didWrite
    }

    /// Makes a completed write available for a single take by the transport.
    @objc public func seal() -> Bool {
        guard case .writable(let bytes) = state else { return false }
        state = .sealed(bytes)
        return true
    }

    /// Releases storage that will not be sent. Calling this more than once is safe.
    @objc public func discard() {
        state = .discarded
    }

    /// Transfers the sealed value to grpc-swift exactly once.
    internal func takeRawMessage() throws -> RawMessage {
        guard case .sealed(let bytes) = state else { throw TakeError.notSealed }
        state = .taken
        return bytes
    }
}
