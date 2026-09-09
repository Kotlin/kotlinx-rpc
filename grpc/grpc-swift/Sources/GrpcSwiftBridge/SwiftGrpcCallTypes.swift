import Foundation

/// The request/response streaming shape of a gRPC method.
@objc(SwiftGrpcMethodType)
public enum SwiftGrpcMethodType: Int, Sendable {
    case unary = 0
    case clientStreaming = 1
    case serverStreaming = 2
    case bidirectionalStreaming = 3
}

/// Compression applied to outbound messages for a call.
@objc(SwiftGrpcCompression)
public enum SwiftGrpcCompression: Int, Sendable {
    case none = 0
    case gzip = 1
}

/// Canonical gRPC status codes.
@objc(SwiftGrpcStatusCode)
public enum SwiftGrpcStatusCode: Int, Sendable {
    case ok = 0
    case cancelled = 1
    case unknown = 2
    case invalidArgument = 3
    case deadlineExceeded = 4
    case notFound = 5
    case alreadyExists = 6
    case permissionDenied = 7
    case resourceExhausted = 8
    case failedPrecondition = 9
    case aborted = 10
    case outOfRange = 11
    case unimplemented = 12
    case `internal` = 13
    case unavailable = 14
    case dataLoss = 15
    case unauthenticated = 16
}

/// The kind of value stored in a metadata entry.
@objc(SwiftGrpcMetadataValueKind)
public enum SwiftGrpcMetadataValueKind: Int, Sendable {
    case invalid = 0
    case string = 1
    case binary = 2
}

/// An Objective-C-compatible, duplicate-preserving gRPC metadata container.
@objc(SwiftGrpcMetadata)
public final class SwiftGrpcMetadata: NSObject, @unchecked Sendable {
    @objc public override init() {
        super.init()
    }

    /// The number of entries, including entries with duplicate keys.
    @objc public var count: Int {
        fatalError("Not yet implemented")
    }

    @objc(addStringValue:forKey:)
    public func addStringValue(_ value: String, forKey key: String) {
        fatalError("Not yet implemented")
    }

    /// Copies one binary metadata value. The pointer is borrowed for this invocation only.
    @objc(addBinaryValue:length:forKey:)
    public func addBinaryValue(
        _ bytes: UnsafeRawPointer?,
        length: Int,
        forKey key: String
    ) {
        fatalError("Not yet implemented")
    }

    @objc(keyAtIndex:)
    public func key(at index: Int) -> String? {
        fatalError("Not yet implemented")
    }

    @objc(valueKindAtIndex:)
    public func valueKind(at index: Int) -> SwiftGrpcMetadataValueKind {
        fatalError("Not yet implemented")
    }

    @objc(stringValueAtIndex:)
    public func stringValue(at index: Int) -> String? {
        fatalError("Not yet implemented")
    }

    /// Provides scoped access to one binary value without an additional copy.
    @objc(withBinaryValueAtIndex:body:)
    @discardableResult
    public func withBinaryValue(
        at index: Int,
        body: (UnsafeRawPointer?, Int) -> Void
    ) -> Bool {
        fatalError("Not yet implemented")
    }
}

/// A serialized request message supplied by Kotlin.
@objc(SwiftGrpcRequestMessage)
public protocol SwiftGrpcRequestMessage: AnyObject {
    /// The exact number of serialized protobuf bytes.
    @objc var length: Int { get }

    /// Fills grpc-swift-owned storage. The pointer must not be retained after this method returns.
    /// The return value indicates whether exactly `length` bytes were written successfully.
    @objc(fillBuffer:capacity:)
    func fillBuffer(_ buffer: UnsafeMutableRawPointer?, capacity: Int) -> Bool
}

/// A pull-based source backed by a Kotlin request `Flow`.
@objc(SwiftGrpcRequestSource)
public protocol SwiftGrpcRequestSource: AnyObject {
    /// Asynchronously supplies the next message.
    ///
    /// A `(nil, nil)` result marks normal completion of the request flow. An error marks failure of
    /// that flow. Only one request may be outstanding, and `completion` must be invoked exactly
    /// once.
    @objc(nextRequestWithCompletion:)
    func nextRequest(
        _ completion: @escaping @Sendable (SwiftGrpcRequestMessage?, NSError?) -> Void
    )

    /// Cancels request-flow collection and completes an outstanding request pull.
    @objc func cancel()
}

/// Terminal gRPC status. A non-OK status is a normal call result, not a bridge error.
@objc(SwiftGrpcStatus)
public final class SwiftGrpcStatus: NSObject, @unchecked Sendable {
    @objc public let code: SwiftGrpcStatusCode
    @objc public let message: String?

    internal init(code: SwiftGrpcStatusCode, message: String?) {
        self.code = code
        self.message = message
        super.init()
    }
}

/// Base class for the strictly ordered response event stream.
@objc(SwiftGrpcCallEvent)
public class SwiftGrpcCallEvent: NSObject, @unchecked Sendable {
    fileprivate override init() {
        super.init()
    }
}

/// Initial response metadata. At most one headers event is emitted.
@objc(SwiftGrpcHeadersEvent)
public final class SwiftGrpcHeadersEvent: SwiftGrpcCallEvent, @unchecked Sendable {
    @objc public var headers: SwiftGrpcMetadata {
        fatalError("Not yet implemented")
    }
}

/// One serialized response message.
@objc(SwiftGrpcMessageEvent)
public final class SwiftGrpcMessageEvent: SwiftGrpcCallEvent, @unchecked Sendable {
    @objc public var length: Int {
        fatalError("Not yet implemented")
    }

    /// Provides scoped access to the grpc-swift-owned message buffer.
    /// The pointer must not be retained or used after `body` returns.
    @objc(withUnsafeBytes:)
    public func withUnsafeBytes(_ body: (UnsafeRawPointer?, Int) -> Void) {
        fatalError("Not yet implemented")
    }
}

/// The terminal event. It is emitted exactly once after all response messages.
@objc(SwiftGrpcClosedEvent)
public final class SwiftGrpcClosedEvent: SwiftGrpcCallEvent, @unchecked Sendable {
    @objc public var status: SwiftGrpcStatus {
        fatalError("Not yet implemented")
    }

    @objc public var trailers: SwiftGrpcMetadata {
        fatalError("Not yet implemented")
    }
}
