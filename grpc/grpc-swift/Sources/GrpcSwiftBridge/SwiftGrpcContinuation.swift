import Foundation

/// The completion convention used for asynchronous Objective-C bridge methods.
///
/// A value (including `nil`, when it represents the end of a stream) is a successful result. An
/// error always takes precedence over the value. Implementations must invoke the completion
/// exactly once.
internal typealias SwiftGrpcCompletion<Value: Sendable> = @Sendable (
    _ value: Value?,
    _ error: NSError?
) -> Void

/// Adapts an Objective-C completion-handler API to Swift concurrency without blocking a thread.
///
/// `onCancellation` must arrange for an outstanding completion to be invoked. This is necessary
/// because checked continuations don't automatically resume when their surrounding task is
/// cancelled.
internal func awaitSwiftGrpcCompletion<Value: Sendable>(
    onCancellation: @escaping @Sendable () -> Void,
    _ register: (_ completion: @escaping SwiftGrpcCompletion<Value>) -> Void
) async throws -> Value? {
    try await withTaskCancellationHandler {
        try await withCheckedThrowingContinuation { continuation in
            register { value, error in
                if let error {
                    continuation.resume(throwing: error)
                } else {
                    continuation.resume(returning: value)
                }
            }
        }
    } onCancel: {
        onCancellation()
    }
}
