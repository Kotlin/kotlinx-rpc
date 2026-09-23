import Foundation
import Synchronization
import XCTest
@testable import GrpcSwiftBridge

final class ResponseEventMailboxTests: XCTestCase {
    // MARK: Delivery

    func testDirectMatchDeliversToWaitingPull() async throws {
        let mailbox = ResponseEventMailbox()
        let pull = Pull()
        let event = headersEvent()

        mailbox.nextEvent(pull.completion)
        XCTAssertEqual(mailbox.phase, .waitingPull)

        try await mailbox.send(event)

        XCTAssertEqual(pull.count, 1)
        XCTAssertTrue(pull.event === event)
        XCTAssertEqual(mailbox.phase, .idle)
    }

    func testOfferBeforePullWaitsUntilClaimed() async throws {
        let mailbox = ResponseEventMailbox()
        let event = headersEvent()
        let producer = Producer(mailbox: mailbox, events: [event])

        try await eventually { mailbox.phase == .offered }
        XCTAssertEqual(producer.sent, 0)

        let pull = Pull()
        mailbox.nextEvent(pull.completion)

        // An offered event is delivered on the pulling thread before `nextEvent` returns.
        XCTAssertEqual(pull.count, 1)
        XCTAssertTrue(pull.event === event)
        XCTAssertEqual(mailbox.phase, .idle)
        try await producer.finish()
        XCTAssertEqual(producer.sent, 1)
    }

    func testProducerCannotAdvancePastOneUnclaimedEvent() async throws {
        let mailbox = ResponseEventMailbox()
        let events = [headersEvent(), headersEvent(), headersEvent()]
        let producer = Producer(mailbox: mailbox, events: events)

        for (index, event) in events.enumerated() {
            try await eventually { mailbox.phase == .offered }
            // Give the producer a chance to run ahead; it must stay parked on its offer.
            try await Task.sleep(for: .milliseconds(20))
            XCTAssertEqual(producer.sent, index)
            XCTAssertEqual(mailbox.phase, .offered)

            let pull = Pull()
            mailbox.nextEvent(pull.completion)
            XCTAssertTrue(pull.event === event)
        }

        try await producer.finish()
        XCTAssertEqual(producer.sent, events.count)
    }

    func testConcurrentPullIsRejectedWithoutDisturbingAcceptedPull() async throws {
        let mailbox = ResponseEventMailbox()
        let accepted = Pull()
        let rejected = Pull()
        let event = headersEvent()

        mailbox.nextEvent(accepted.completion)
        mailbox.nextEvent(rejected.completion)

        XCTAssertEqual(rejected.count, 1)
        assertError(rejected.error, is: CallBridgeError.invalidEventPull)
        XCTAssertEqual(accepted.count, 0)
        XCTAssertEqual(mailbox.phase, .waitingPull)

        try await mailbox.send(event)

        XCTAssertEqual(accepted.count, 1)
        XCTAssertTrue(accepted.event === event)
        XCTAssertEqual(rejected.count, 1)
    }

    func testDirectlyDeliveredClosedEventIsTerminal() async throws {
        let mailbox = ResponseEventMailbox()
        let pull = Pull()
        let closed = closedEvent()

        mailbox.nextEvent(pull.completion)
        try await mailbox.send(closed)

        XCTAssertTrue(pull.event === closed)
        XCTAssertEqual(mailbox.phase, .terminalDelivered)
        try await assertRejectsLaterPullsAndSends(mailbox)
    }

    func testOfferedClosedEventIsTerminalOnceClaimed() async throws {
        let mailbox = ResponseEventMailbox()
        let closed = closedEvent()
        let producer = Producer(mailbox: mailbox, events: [closed])

        try await eventually { mailbox.phase == .offered }
        let pull = Pull()
        mailbox.nextEvent(pull.completion)

        XCTAssertTrue(pull.event === closed)
        XCTAssertEqual(mailbox.phase, .terminalDelivered)
        try await producer.finish()
        try await assertRejectsLaterPullsAndSends(mailbox)
    }

    func testFailureBeforePullIsDeliveredOnce() async throws {
        let mailbox = ResponseEventMailbox()
        let failure = NSError(domain: "test", code: 42)

        mailbox.fail(failure)
        XCTAssertEqual(mailbox.phase, .terminalPending)

        let pull = Pull()
        mailbox.nextEvent(pull.completion)
        XCTAssertEqual(pull.count, 1)
        XCTAssertTrue(pull.error === failure)
        XCTAssertEqual(mailbox.phase, .terminalDelivered)
        try await assertRejectsLaterPullsAndSends(mailbox)
    }

    func testFirstTerminalResultWins() {
        let mailbox = ResponseEventMailbox()
        let first = NSError(domain: "test", code: 1)

        mailbox.fail(first)
        mailbox.fail(NSError(domain: "test", code: 2))
        mailbox.cancel()

        let pull = Pull()
        mailbox.nextEvent(pull.completion)
        XCTAssertTrue(pull.error === first)
    }

    // MARK: Reentrancy

    func testPullFromOfferedClaimCallbackIsServedByTheProducer() async throws {
        let mailbox = ResponseEventMailbox()
        let first = headersEvent()
        let second = headersEvent()
        let producer = Producer(mailbox: mailbox, events: [first, second])
        let reentrant = Pull()
        let completedDuringCallback = Mutex<Int?>(nil)

        try await eventually { mailbox.phase == .offered }
        let pull = Pull { _, _ in
            mailbox.nextEvent(reentrant.completion)
            completedDuringCallback.withLock { $0 = reentrant.count }
        }
        mailbox.nextEvent(pull.completion)

        XCTAssertTrue(pull.event === first)
        XCTAssertEqual(completedDuringCallback.withLock { $0 }, 0)
        try await eventually { reentrant.count == 1 }
        XCTAssertTrue(reentrant.event === second)
        try await producer.finish()
    }

    func testPullFromDirectMatchCallbackWaitsForTheNextSend() async throws {
        let mailbox = ResponseEventMailbox()
        let first = headersEvent()
        let second = headersEvent()
        let reentrant = Pull()
        let completedDuringCallback = Mutex<Int?>(nil)

        let pull = Pull { _, _ in
            mailbox.nextEvent(reentrant.completion)
            completedDuringCallback.withLock { $0 = reentrant.count }
        }
        mailbox.nextEvent(pull.completion)
        try await mailbox.send(first)

        XCTAssertTrue(pull.event === first)
        XCTAssertEqual(completedDuringCallback.withLock { $0 }, 0)
        XCTAssertEqual(mailbox.phase, .waitingPull)

        try await mailbox.send(second)
        XCTAssertTrue(reentrant.event === second)
    }

    func testCancelFromOfferedClaimCallbackKeepsTheClaimedEvent() async throws {
        let mailbox = ResponseEventMailbox()
        let first = headersEvent()
        let producer = Producer(mailbox: mailbox, events: [first, headersEvent()])

        try await eventually { mailbox.phase == .offered }
        let pull = Pull { _, _ in mailbox.cancel() }
        mailbox.nextEvent(pull.completion)

        XCTAssertEqual(pull.count, 1)
        XCTAssertTrue(pull.event === first)
        // The claimed send succeeds; the producer observes cancellation on its next send.
        await assertThrowsCancellation { try await producer.finish() }
        XCTAssertEqual(producer.sent, 1)

        let late = Pull()
        mailbox.nextEvent(late.completion)
        assertCancellation(late.error)
        try await assertRejectsLaterPullsAndSends(mailbox)
    }

    func testCancelFromDirectMatchCallbackEndsTheMailbox() async throws {
        let mailbox = ResponseEventMailbox()
        let first = headersEvent()

        let pull = Pull { _, _ in mailbox.cancel() }
        mailbox.nextEvent(pull.completion)
        try await mailbox.send(first)

        XCTAssertTrue(pull.event === first)
        XCTAssertEqual(mailbox.phase, .terminalPending)
        await assertThrowsCancellation { try await mailbox.send(headersEvent()) }
    }

    // MARK: Cancellation

    func testCancelBeforeAnyPull() async throws {
        let mailbox = ResponseEventMailbox()

        mailbox.cancel()
        await assertThrowsCancellation { try await mailbox.send(headersEvent()) }

        let pull = Pull()
        mailbox.nextEvent(pull.completion)
        XCTAssertEqual(pull.count, 1)
        assertCancellation(pull.error)
        try await assertRejectsLaterPullsAndSends(mailbox)
    }

    func testCancelCompletesWaitingPull() async throws {
        let mailbox = ResponseEventMailbox()
        let pull = Pull()

        mailbox.nextEvent(pull.completion)
        mailbox.cancel()

        XCTAssertEqual(pull.count, 1)
        assertCancellation(pull.error)
        XCTAssertEqual(mailbox.phase, .terminalDelivered)
        try await assertRejectsLaterPullsAndSends(mailbox)
    }

    func testCancelReleasesOfferedProducerAndDropsItsEvent() async throws {
        let mailbox = ResponseEventMailbox()
        let producer = Producer(mailbox: mailbox, events: [headersEvent()])

        try await eventually { mailbox.phase == .offered }
        mailbox.cancel()

        await assertThrowsCancellation { try await producer.finish() }
        XCTAssertEqual(producer.sent, 0)
        XCTAssertEqual(mailbox.phase, .terminalPending)

        let pull = Pull()
        mailbox.nextEvent(pull.completion)
        XCTAssertNil(pull.event)
        assertCancellation(pull.error)
    }

    func testCancelAfterClosedIsClaimedIsIgnored() async throws {
        let mailbox = ResponseEventMailbox()
        let pull = Pull()
        let closed = closedEvent()

        mailbox.nextEvent(pull.completion)
        try await mailbox.send(closed)
        mailbox.cancel()

        XCTAssertEqual(pull.count, 1)
        XCTAssertTrue(pull.event === closed)
        XCTAssertEqual(mailbox.phase, .terminalDelivered)
    }

    // MARK: Producer-task cancellation

    func testProducerTaskCancellationWithdrawsOfferWithoutEndingMailbox() async throws {
        let mailbox = ResponseEventMailbox()
        let producer = Producer(mailbox: mailbox, events: [headersEvent()])

        try await eventually { mailbox.phase == .offered }
        producer.cancel()

        await assertThrowsCancellation { try await producer.finish() }
        XCTAssertEqual(mailbox.phase, .idle)

        // The runner can still publish the status that grpc-swift reports for the deadline.
        let pull = Pull()
        let closed = closedEvent()
        mailbox.nextEvent(pull.completion)
        try await mailbox.send(closed)
        XCTAssertEqual(pull.count, 1)
        XCTAssertTrue(pull.event === closed)
    }

    func testCancelledProducerTaskDoesNotStoreAnOffer() async throws {
        let mailbox = ResponseEventMailbox()

        let producer = Task {
            withUnsafeCurrentTask { $0?.cancel() }
            try await mailbox.send(headersEvent())
        }

        await assertThrowsCancellation { try await producer.value }
        XCTAssertEqual(mailbox.phase, .idle)
    }

    func testWithdrawAfterClaimLeavesTheEventDelivered() async throws {
        let mailbox = ResponseEventMailbox()
        let event = headersEvent()
        let producer = Producer(mailbox: mailbox, events: [event])

        try await eventually { mailbox.phase == .offered }
        let pull = Pull()
        mailbox.nextEvent(pull.completion)
        mailbox.withdraw()
        producer.cancel()
        _ = try? await producer.finish()

        XCTAssertEqual(pull.count, 1)
        XCTAssertTrue(pull.event === event)
        XCTAssertEqual(mailbox.phase, .idle)
    }

    // MARK: Races

    func testConcurrentPullsCancellationAndWithdrawalKeepInvariants() async throws {
        for iteration in 0..<2_000 {
            try await withTimeout(.seconds(5)) {
                try await runRace(seed: UInt64(iteration))
            }
        }
    }
}

// MARK: - Race scenario

/// Runs one randomized race between a producer, a consumer, a competing puller and a canceller.
///
/// The producer mirrors ``CallRunner``: a child task sends events and may be cancelled (like
/// grpc-swift's deadline group), after which the parent publishes a closed event.
private func runRace(seed: UInt64) async throws {
    var random = SplitMix64(seed: seed)
    let eventCount = Int.random(in: 0...8, using: &random)
    let cancelMailboxAfter = Int.random(in: 0...20, using: &random)
    let cancelProducerAfter = Int.random(in: 0...20, using: &random)
    let competingPulls = Int.random(in: 0...4, using: &random)

    let mailbox = ResponseEventMailbox()
    let events = (0..<eventCount).map { _ in headersEvent() }
    let deadlineClosed = closedEvent()
    let normalClosed = closedEvent()
    let log = DeliveryLog()

    let producer = Task {
        let child = Task {
            for event in events {
                try await mailbox.send(event)
            }
            try await mailbox.send(normalClosed)
        }
        let cancelChild = Task {
            for _ in 0..<cancelProducerAfter { await Task.yield() }
            child.cancel()
        }
        do {
            try await child.value
        } catch {
            do {
                try await mailbox.send(deadlineClosed)
            } catch {
                mailbox.cancel()
            }
        }
        cancelChild.cancel()
        mailbox.fail(CallBridgeError.missingTerminalEvent as NSError)
    }

    let canceller = Task {
        for _ in 0..<cancelMailboxAfter { await Task.yield() }
        mailbox.cancel()
    }

    let competitor = Task {
        for _ in 0..<competingPulls {
            await Task.yield()
            mailbox.nextEvent(log.completion(pullID: -1))
        }
    }

    // Pull until some puller, possibly the competitor, has received the terminal result.
    var pullID = 0
    while !log.terminalDelivered {
        let result = await withCheckedContinuation { continuation in
            mailbox.nextEvent(log.completion(pullID: pullID) { event, error in
                continuation.resume(returning: (event, error))
            })
        }
        pullID += 1
        if let error = result.1, isError(error, CallBridgeError.invalidEventPull) {
            // The competitor holds the pull or already received the terminal result.
            await Task.yield()
        }
    }

    await producer.value
    await canceller.value
    await competitor.value

    log.assertInvariants(events: events, closedEvents: [normalClosed, deadlineClosed])
}

/// Records every pull outcome in delivery order.
private final class DeliveryLog: Sendable {
    struct Entry: Sendable {
        let pullID: Int
        let event: SwiftGrpcCallEvent?
        let error: NSError?

        var isCompetitor: Bool { self.pullID < 0 }
    }

    private let state = Mutex<(entries: [Entry], counts: [Int: Int], terminal: Bool)>(([], [:], false))

    var terminalDelivered: Bool { self.state.withLock { $0.terminal } }

    func completion(
        pullID: Int,
        then next: (@Sendable (SwiftGrpcCallEvent?, NSError?) -> Void)? = nil
    ) -> ResponseEventMailbox.Completion {
        // Competing pulls share one ID; give each a unique key for the exactly-once check.
        let key = pullID >= 0 ? pullID : -1 - Int(UInt32.random(in: 0...UInt32.max))
        return { event, error in
            self.state.withLock { state in
                state.entries.append(Entry(pullID: pullID, event: event, error: error))
                state.counts[key, default: 0] += 1
                let rejected = error.map { isError($0, CallBridgeError.invalidEventPull) } ?? false
                if !rejected && (event == nil || event is SwiftGrpcClosedEvent) {
                    state.terminal = true
                }
            }
            next?(event, error)
        }
    }

    func assertInvariants(
        events: [SwiftGrpcCallEvent],
        closedEvents: [SwiftGrpcClosedEvent],
        file: StaticString = #filePath,
        line: UInt = #line
    ) {
        let (entries, counts, _) = self.state.withLock { $0 }
        XCTAssertTrue(counts.values.allSatisfy { $0 == 1 }, "a pull completed more than once", file: file, line: line)

        let accepted = entries.filter { entry in
            guard let error = entry.error else { return true }
            return !isError(error, CallBridgeError.invalidEventPull)
        }
        let delivered = accepted.compactMap(\.event).filter { !($0 is SwiftGrpcClosedEvent) }
        XCTAssertLessThanOrEqual(delivered.count, events.count, file: file, line: line)
        for (actual, expected) in zip(delivered, events) {
            XCTAssertTrue(actual === expected, "events were reordered, duplicated or lost", file: file, line: line)
        }

        let terminal = accepted.filter { entry in
            entry.event == nil || entry.event is SwiftGrpcClosedEvent
        }
        XCTAssertEqual(terminal.count, 1, "expected exactly one terminal result", file: file, line: line)
        // Callbacks run after the lock is released, so the log is ordered by callback time, not by
        // claim time. A pull may claim an event, and a cancellation plus a concurrent pull may
        // complete before the first callback runs. Events are sequenced by the producer, so only
        // such a cancellation error can overtake a claimed event, by at most one.
        let description = accepted.map { entry in
            let kind = entry.event.map { $0 is SwiftGrpcClosedEvent ? "closed" : "event" } ?? "error"
            return "\(entry.isCompetitor ? "competitor" : "consumer"):\(kind)"
        }.joined(separator: ", ")
        if let terminalIndex = accepted.firstIndex(where: { $0.event == nil || $0.event is SwiftGrpcClosedEvent }) {
            let terminalEntry = accepted[terminalIndex]
            let later = accepted[(terminalIndex + 1)...]
            if terminalEntry.event != nil {
                XCTAssertTrue(later.isEmpty, "an event was delivered after Closed: \(description)", file: file, line: line)
            } else {
                XCTAssertLessThanOrEqual(
                    later.count,
                    1,
                    "events were delivered after cancellation: \(description)",
                    file: file,
                    line: line
                )
            }
        }

        // The consumer pulls sequentially, so its own log order is its observed order.
        let consumer = accepted.filter { !$0.isCompetitor }
        if let consumerTerminal = consumer.firstIndex(where: { $0.event == nil || $0.event is SwiftGrpcClosedEvent }) {
            XCTAssertEqual(
                consumerTerminal,
                consumer.count - 1,
                "the consumer received an event after its terminal result: \(description)",
                file: file,
                line: line
            )
        }
        if let closed = terminal.first?.event {
            XCTAssertTrue(closedEvents.contains { $0 === closed }, file: file, line: line)
        } else if let error = terminal.first?.error {
            XCTAssertEqual(error.domain, (CancellationError() as NSError).domain, file: file, line: line)
        }
        if let closed = terminal.first?.event, closed === closedEvents[0] {
            XCTAssertEqual(delivered.count, events.count, "normal close skipped an event", file: file, line: line)
        }
    }
}

// MARK: - Helpers

/// Records the completions of one pull.
private final class Pull: Sendable {
    private let results = Mutex<[(SwiftGrpcCallEvent?, NSError?)]>([])
    private let onCompletion: (@Sendable (SwiftGrpcCallEvent?, NSError?) -> Void)?

    init(onCompletion: (@Sendable (SwiftGrpcCallEvent?, NSError?) -> Void)? = nil) {
        self.onCompletion = onCompletion
    }

    var completion: ResponseEventMailbox.Completion {
        { event, error in
            self.results.withLock { $0.append((event, error)) }
            self.onCompletion?(event, error)
        }
    }

    var count: Int { self.results.withLock { $0.count } }
    var event: SwiftGrpcCallEvent? { self.results.withLock { $0.first?.0 } }
    var error: NSError? { self.results.withLock { $0.first?.1 } }
}

/// Sends events from its own task and counts completed sends.
private final class Producer: Sendable {
    private let task: Mutex<Task<Void, any Error>?> = Mutex(nil)
    private let sentCount = Atomic<Int>(0)

    init(mailbox: ResponseEventMailbox, events: [SwiftGrpcCallEvent]) {
        let task = Task { [self] in
            for event in events {
                try await mailbox.send(event)
                self.sentCount.add(1, ordering: .relaxed)
            }
        }
        self.task.withLock { $0 = task }
    }

    var sent: Int { self.sentCount.load(ordering: .relaxed) }

    func cancel() {
        self.task.withLock { $0 }?.cancel()
    }

    func finish() async throws {
        try await self.task.withLock { $0 }!.value
    }
}

private struct SplitMix64: RandomNumberGenerator {
    private var state: UInt64

    init(seed: UInt64) {
        self.state = seed
    }

    mutating func next() -> UInt64 {
        self.state &+= 0x9E37_79B9_7F4A_7C15
        var z = self.state
        z = (z ^ (z >> 30)) &* 0xBF58_476D_1CE4_E5B9
        z = (z ^ (z >> 27)) &* 0x94D0_49BB_1331_11EB
        return z ^ (z >> 31)
    }
}

private struct TimeoutError: Error {}

private final class Outcome: Sendable {
    let result = Mutex<Result<Void, any Error>?>(nil)
}

/// Fails instead of hanging when `operation` does not finish. A stuck operation is leaked, since
/// it may not respond to cancellation.
private func withTimeout(
    _ timeout: Duration,
    _ operation: @escaping @Sendable () async throws -> Void
) async throws {
    let outcome = Outcome()
    Task {
        let result: Result<Void, any Error>
        do {
            try await operation()
            result = .success(())
        } catch {
            result = .failure(error)
        }
        outcome.result.withLock { $0 = result }
    }

    let deadline = ContinuousClock.now + timeout
    while true {
        if let result = outcome.result.withLock({ $0 }) {
            return try result.get()
        }
        guard ContinuousClock.now < deadline else {
            XCTFail("operation did not finish within \(timeout)")
            throw TimeoutError()
        }
        try await Task.sleep(for: .microseconds(100))
    }
}

private func eventually(
    timeout: Duration = .seconds(5),
    file: StaticString = #filePath,
    line: UInt = #line,
    _ condition: () -> Bool
) async throws {
    let deadline = ContinuousClock.now + timeout
    while !condition() {
        guard ContinuousClock.now < deadline else {
            XCTFail("condition was not met within \(timeout)", file: file, line: line)
            throw TimeoutError()
        }
        try await Task.sleep(for: .milliseconds(1))
    }
}

private func headersEvent() -> SwiftGrpcHeadersEvent {
    SwiftGrpcHeadersEvent(headers: SwiftGrpcMetadata())
}

private func closedEvent() -> SwiftGrpcClosedEvent {
    SwiftGrpcClosedEvent(status: SwiftGrpcStatus(code: .ok, message: nil), trailers: SwiftGrpcMetadata())
}

private func isError(_ error: NSError, _ expected: CallBridgeError) -> Bool {
    let expected = expected as NSError
    return error.domain == expected.domain && error.code == expected.code
}

private func assertError(
    _ error: NSError?,
    is expected: CallBridgeError,
    file: StaticString = #filePath,
    line: UInt = #line
) {
    guard let error else {
        return XCTFail("expected \(expected), got no error", file: file, line: line)
    }
    XCTAssertTrue(isError(error, expected), "expected \(expected), got \(error)", file: file, line: line)
}

private func assertCancellation(_ error: NSError?, file: StaticString = #filePath, line: UInt = #line) {
    XCTAssertEqual(error?.domain, (CancellationError() as NSError).domain, file: file, line: line)
}

private func assertThrowsCancellation(
    file: StaticString = #filePath,
    line: UInt = #line,
    _ operation: () async throws -> Void
) async {
    do {
        try await operation()
        XCTFail("expected CancellationError", file: file, line: line)
    } catch is CancellationError {
        // Expected.
    } catch {
        XCTFail("expected CancellationError, got \(error)", file: file, line: line)
    }
}

private func assertRejectsLaterPullsAndSends(
    _ mailbox: ResponseEventMailbox,
    file: StaticString = #filePath,
    line: UInt = #line
) async throws {
    let pull = Pull()
    mailbox.nextEvent(pull.completion)
    XCTAssertEqual(pull.count, 1, file: file, line: line)
    assertError(pull.error, is: CallBridgeError.invalidEventPull, file: file, line: line)
    await assertThrowsCancellation(file: file, line: line) {
        try await mailbox.send(headersEvent())
    }
}
