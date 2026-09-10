import Foundation
import GRPCCore
import GRPCNIOTransportHTTP2TransportServices

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

/// Receives metadata entries synchronously in their stored order, including duplicate keys.
@objc(SwiftGrpcMetadataVisitor)
public protocol SwiftGrpcMetadataVisitor: AnyObject {
    /// Receives one string entry.
    @objc(visitStringWithKey:value:)
    func visitString(key: String, value: String)

    /// Receives one binary entry. The bytes are borrowed only for this callback and must not be retained.
    @objc(visitBinaryWithKey:bytes:length:)
    func visitBinary(key: String, bytes: UnsafeRawPointer?, length: Int)
}

/// An Objective-C-compatible, duplicate-preserving gRPC metadata container.
///
/// This class declaratively only exposes a minimal API that is required for
/// fast metadata copying between Kotlin and Swift.
@objc(SwiftGrpcMetadata)
public final class SwiftGrpcMetadata: NSObject, @unchecked Sendable {
    var metadata: Metadata
    
    @objc public override init() {
        self.metadata = Metadata()
        super.init()
    }
    
    public init (_ metadata: Metadata) {
        self.metadata = metadata
        super.init()
    }

    /// The number of entries, including entries with duplicate keys.
    @objc public var count: Int {
        metadata.count
    }

    /// Visits every application-visible entry synchronously without retaining the visitor or
    /// copying binary buffers.
    @objc(visitEntries:)
    public func visitEntries(_ visitor: any SwiftGrpcMetadataVisitor) {
        for (key, value) in metadata where
        Self.shouldPropagateMetadataEntry(forKey: key) {
            switch value {
            case .string(let string):
                visitor.visitString(key: key, value: string)
            case .binary(let binary):
                binary.withUnsafeBytes { buffer in
                    visitor.visitBinary(key: key, bytes: buffer.baseAddress, length: buffer.count)
                }
            }
        }
    }

    private static func shouldPropagateMetadataEntry(forKey key: String) -> Bool {
        // HTTP/2 pseudo-headers carry transport state and are not application metadata.
        !key.hasPrefix(":")
    }

    @objc(addStringValue:forKey:)
    public func addStringValue(_ value: String, forKey key: String) {
        metadata.addString(value, forKey: key)
    }

    /// Copies one binary metadata value. The pointer is borrowed for this invocation only.
    @objc(addBinaryValue:length:forKey:)
    public func addBinaryValue(
        _ bytes: UnsafeRawPointer?,
        length: Int,
        forKey key: String
    ) {
        precondition(length >= 0, "Binary metadata length must be nonnegative")
        precondition(bytes != nil || length == 0, "Nonempty metadata requires bytes")
        precondition(key.hasSuffix("-bin"), "Binary metadata keys must end in -bin")
        
        let buffer = UnsafeRawBufferPointer(start: bytes, count: length)
        metadata.addBinary(Array(buffer), forKey: key)
    }
}

/// A serialized request message supplied by Kotlin.
@objc(SwiftGrpcRequestMessage)
public protocol SwiftGrpcRequestMessage: AnyObject, Sendable {
    /// The exact number of serialized protobuf bytes.
    @objc var length: Int { get }

    /// Fills grpc-swift-owned storage. The pointer must not be retained after this method returns.
    /// The return value indicates whether exactly `length` bytes were written successfully.
    @objc(fillBuffer:capacity:)
    func fillBuffer(_ buffer: UnsafeMutableRawPointer?, capacity: Int) -> Bool
}

/// A pull-based source backed by a Kotlin request `Flow`.
@objc(SwiftGrpcRequestSource)
public protocol SwiftGrpcRequestSource: AnyObject, Sendable {
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
    @objc public let headers: SwiftGrpcMetadata
    
    internal init(headers: SwiftGrpcMetadata) {
        self.headers = headers
        super.init()
    }
}

/// One serialized response message.
@objc(SwiftGrpcMessageEvent)
public final class SwiftGrpcMessageEvent: SwiftGrpcCallEvent, @unchecked Sendable {
    let message: GRPCNIOTransportBytes
    
    internal init(message: GRPCNIOTransportBytes) {
        self.message = message
    }
    
    @objc public var length: Int {
        message.count
    }

    /// Provides scoped access to the grpc-swift-owned message buffer.
    /// The pointer must not be retained or used after `body` returns.
    @objc(withUnsafeBytes:)
    public func withUnsafeBytes(_ body: (UnsafeRawPointer?, Int) -> Void) {
        message.withUnsafeBytes { buffer in
            body(buffer.baseAddress, buffer.count)
        }
    }
}

/// The terminal event. It is emitted exactly once after all response messages.
@objc(SwiftGrpcClosedEvent)
public final class SwiftGrpcClosedEvent: SwiftGrpcCallEvent, @unchecked Sendable {
    @objc public let status: SwiftGrpcStatus
    @objc public let trailers: SwiftGrpcMetadata
    
    internal init(status: SwiftGrpcStatus, trailers: SwiftGrpcMetadata) {
        self.status = status
        self.trailers = trailers
    }
}
