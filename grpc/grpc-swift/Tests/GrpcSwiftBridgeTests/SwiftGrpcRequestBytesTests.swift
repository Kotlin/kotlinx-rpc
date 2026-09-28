import Foundation
import XCTest
@testable import GrpcSwiftBridge

final class SwiftGrpcRequestBytesTests: XCTestCase {
    func testWriteSealAndTakeWithoutCopy() throws {
        let storage = SwiftGrpcRequestBytes(count: 3)
        var writtenAddress: UnsafeMutableRawPointer?

        XCTAssertTrue(storage.withWritableBytes { pointer, count in
            XCTAssertEqual(count, 3)
            writtenAddress = pointer
            pointer!.storeBytes(of: UInt8(1), as: UInt8.self)
            pointer!.advanced(by: 1).storeBytes(of: UInt8(2), as: UInt8.self)
            pointer!.advanced(by: 2).storeBytes(of: UInt8(3), as: UInt8.self)
            return true
        })
        XCTAssertTrue(storage.seal())
        XCTAssertFalse(storage.seal())

        let message = try storage.makeRawMessage()
        XCTAssertEqual(message.withUnsafeBytes { Array($0) }, [1, 2, 3])
        XCTAssertEqual(message.withUnsafeBytes { $0.baseAddress }, UnsafeRawPointer(writtenAddress))
        XCTAssertThrowsError(try storage.makeRawMessage())
        XCTAssertFalse(storage.withWritableBytes { _, _ in true })
    }

    func testFailedWriteCannotBeSealedOrTaken() {
        let storage = SwiftGrpcRequestBytes(count: 1)
        XCTAssertFalse(storage.withWritableBytes { pointer, _ in
            pointer!.storeBytes(of: UInt8(42), as: UInt8.self)
            return false
        })
        XCTAssertFalse(storage.seal())
        XCTAssertThrowsError(try storage.makeRawMessage())
        XCTAssertFalse(storage.withWritableBytes { _, _ in true })
    }

    func testDiscardFromEveryState() throws {
        let writable = SwiftGrpcRequestBytes(count: 1)
        writable.discard()
        writable.discard()
        XCTAssertFalse(writable.seal())
        XCTAssertThrowsError(try writable.makeRawMessage())

        let writing = SwiftGrpcRequestBytes(count: 1)
        XCTAssertFalse(writing.withWritableBytes { _, _ in
            writing.discard()
            return true
        })
        XCTAssertFalse(writing.seal())
        XCTAssertThrowsError(try writing.makeRawMessage())

        let sealed = SwiftGrpcRequestBytes(count: 1)
        XCTAssertTrue(sealed.seal())
        sealed.discard()
        XCTAssertThrowsError(try sealed.makeRawMessage())

        let taken = SwiftGrpcRequestBytes(count: 0)
        XCTAssertTrue(taken.seal())
        let message = try taken.makeRawMessage()
        taken.discard()
        XCTAssertEqual(message.count, 0)
        XCTAssertThrowsError(try taken.makeRawMessage())
    }

    func testNestedWriteFailsWithoutDisturbingOuterWrite() throws {
        let storage = SwiftGrpcRequestBytes(count: 1)
        XCTAssertTrue(storage.withWritableBytes { pointer, _ in
            XCTAssertFalse(storage.withWritableBytes { _, _ in true })
            XCTAssertFalse(storage.seal())
            pointer!.storeBytes(of: UInt8(7), as: UInt8.self)
            return true
        })
        XCTAssertTrue(storage.seal())
        XCTAssertEqual(try storage.makeRawMessage().withUnsafeBytes { Array($0) }, [7])
    }

    func testZeroLengthUsesNoPointer() throws {
        let storage = SwiftGrpcRequestBytes(count: 0)
        XCTAssertTrue(storage.withWritableBytes { pointer, count in
            XCTAssertNil(pointer)
            XCTAssertEqual(count, 0)
            return true
        })
        XCTAssertTrue(storage.seal())
        XCTAssertEqual(try storage.makeRawMessage().count, 0)
    }

    func testOtherRequestMessagesStillUseCopyPath() throws {
        let request = CopyingRequestMessage(bytes: [4, 5, 6])
        let message = try request.makeRawMessage()
        XCTAssertEqual(message.withUnsafeBytes { Array($0) }, [4, 5, 6])
        XCTAssertEqual(request.fillCount, 1)
    }
}

private final class CopyingRequestMessage: SwiftGrpcRequestMessage, @unchecked Sendable {
    let bytes: [UInt8]
    var fillCount = 0

    init(bytes: [UInt8]) {
        self.bytes = bytes
    }

    var length: Int { bytes.count }

    func fillBuffer(_ buffer: UnsafeMutableRawPointer?, capacity: Int) -> Bool {
        guard capacity == bytes.count else { return false }
        fillCount += 1
        bytes.withUnsafeBytes { source in
            if capacity > 0 {
                buffer!.copyMemory(from: source.baseAddress!, byteCount: capacity)
            }
        }
        return true
    }
}
