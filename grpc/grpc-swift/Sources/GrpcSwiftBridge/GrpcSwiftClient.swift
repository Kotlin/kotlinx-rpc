import Foundation
import GRPCCore
import GRPCNIOTransportHTTP2TransportServices

/// Configuration used to create a ``SwiftGrpcClient``.
///
/// A negative `keepAliveTimeMilliseconds` disables keepalive. The remaining keepalive values are
/// only used when keepalive is enabled.
@objc(SwiftGrpcClientConfiguration)
public final class SwiftGrpcClientConfiguration: NSObject {
    /// The DNS hostname to connect to.
    @objc public let host: String

    /// The TCP port to connect to.
    @objc public let port: Int

    /// Whether to use an unencrypted connection instead of TLS with the system trust store.
    @objc public let plaintext: Bool

    /// Overrides both the HTTP/2 `:authority` pseudo-header and the TLS SNI hostname.
    @objc public var overrideAuthority: String?

    /// A custom user-agent prefix. Calls created by the client add the runtime token.
    @objc public var userAgent: String?

    /// The keepalive interval in milliseconds, or a negative value to disable keepalive.
    @objc public var keepAliveTimeMilliseconds: Int64 = -1

    /// The time to wait for a keepalive acknowledgement, in milliseconds.
    @objc public var keepAliveTimeoutMilliseconds: Int64 = 20_000

    /// Whether keepalive pings may be sent while there are no active calls.
    @objc public var keepAliveWithoutCalls: Bool = false

    @objc(initWithHost:port:plaintext:)
    public init(host: String, port: Int, plaintext: Bool) {
        self.host = host
        self.port = port
        self.plaintext = plaintext
        super.init()
    }
}

/// A long-lived grpc-swift client using Apple's Network.framework transport.
///
/// This is the Swift-side equivalent of a managed channel. It owns the grpc-swift client and its
/// connection task for the complete lifetime of the Kotlin managed channel.
@objc(SwiftGrpcClient)
public final class SwiftGrpcClient: NSObject, @unchecked Sendable {
    internal typealias Transport = HTTP2ClientTransport.TransportServices
    internal typealias Client = GRPCClient<Transport>

    internal let client: Client
    internal let userAgent: String?

    private let connectionTask: Task<Void, Never>

    @objc(initWithConfiguration:error:)
    public init(configuration: SwiftGrpcClientConfiguration) throws {
        var transportConfiguration = Transport.Config.defaults
        transportConfiguration.http2.authority = configuration.overrideAuthority
        transportConfiguration.compression = HTTP2ClientTransport.Config.Compression(
            algorithm: .none,
            enabledAlgorithms: [.gzip]
        )

        if configuration.keepAliveTimeMilliseconds >= 0 {
            transportConfiguration.connection.keepalive = HTTP2ClientTransport.Config.Keepalive(
                time: .milliseconds(configuration.keepAliveTimeMilliseconds),
                timeout: .milliseconds(configuration.keepAliveTimeoutMilliseconds),
                allowWithoutCalls: configuration.keepAliveWithoutCalls
            )
        }

        let transport = try Transport(
            target: .dns(host: configuration.host, port: configuration.port),
            transportSecurity: configuration.plaintext ? .plaintext : .tls,
            config: transportConfiguration
        )
        let client = Client(transport: transport)

        self.client = client
        self.userAgent = configuration.userAgent
        self.connectionTask = Task {
            // Connection errors are surfaced by individual RPCs. The task exists to keep the
            // transport running until graceful or forceful shutdown.
            try? await client.runConnections()
        }
        super.init()
    }

    /// Stops accepting new calls and lets calls already in progress finish.
    @objc public func beginGracefulShutdown() {
        self.client.beginGracefulShutdown()
    }

    /// Abruptly stops the client and cancels calls in progress.
    @objc public func shutdownNow() {
        self.connectionTask.cancel()
    }

    deinit {
        self.client.beginGracefulShutdown()
        self.connectionTask.cancel()
    }
}
