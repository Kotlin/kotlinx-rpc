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
        XCTAssertFalse(storage.isDiscarded)
        XCTAssertFalse(storage.seal())

        let message = try storage.takeRawMessage()
        XCTAssertEqual(message.withUnsafeBytes { Array($0) }, [1, 2, 3])
        XCTAssertEqual(message.withUnsafeBytes { $0.baseAddress }, UnsafeRawPointer(writtenAddress))
        XCTAssertThrowsError(try storage.takeRawMessage())
        XCTAssertFalse(storage.withWritableBytes { _, _ in true })
    }

    func testFailedWriteCannotBeSealedOrTaken() {
        let storage = SwiftGrpcRequestBytes(count: 1)
        XCTAssertFalse(storage.withWritableBytes { pointer, _ in
            pointer!.storeBytes(of: UInt8(42), as: UInt8.self)
            return false
        })
        XCTAssertFalse(storage.seal())
        XCTAssertThrowsError(try storage.takeRawMessage())
        XCTAssertFalse(storage.withWritableBytes { _, _ in true })
    }

    func testDiscardFromEveryState() throws {
        let writable = SwiftGrpcRequestBytes(count: 1)
        writable.discard()
        writable.discard()
        XCTAssertTrue(writable.isDiscarded)
        XCTAssertFalse(writable.seal())
        XCTAssertThrowsError(try writable.takeRawMessage())

        let writing = SwiftGrpcRequestBytes(count: 1)
        XCTAssertFalse(writing.withWritableBytes { _, _ in
            writing.discard()
            return true
        })
        XCTAssertTrue(writing.isDiscarded)
        XCTAssertFalse(writing.seal())
        XCTAssertThrowsError(try writing.takeRawMessage())

        let sealed = SwiftGrpcRequestBytes(count: 1)
        XCTAssertTrue(sealed.seal())
        sealed.discard()
        XCTAssertTrue(sealed.isDiscarded)
        XCTAssertThrowsError(try sealed.takeRawMessage())

        let taken = SwiftGrpcRequestBytes(count: 0)
        XCTAssertTrue(taken.seal())
        let message = try taken.takeRawMessage()
        taken.discard()
        XCTAssertTrue(taken.isDiscarded)
        XCTAssertEqual(message.count, 0)
        XCTAssertThrowsError(try taken.takeRawMessage())
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
        XCTAssertEqual(try storage.takeRawMessage().withUnsafeBytes { Array($0) }, [7])
    }

    func testZeroLengthUsesNoPointer() throws {
        let storage = SwiftGrpcRequestBytes(count: 0)
        XCTAssertTrue(storage.withWritableBytes { pointer, count in
            XCTAssertNil(pointer)
            XCTAssertEqual(count, 0)
            return true
        })
        XCTAssertTrue(storage.seal())
        XCTAssertEqual(try storage.takeRawMessage().count, 0)
    }

    func testLargeMessageRetainsWrittenBytes() throws {
        let count = 4 * 1024 * 1024 - 1024
        let storage = SwiftGrpcRequestBytes(count: count)
        XCTAssertTrue(storage.withWritableBytes { pointer, capacity in
            XCTAssertEqual(capacity, count)
            let bytes = pointer!.assumingMemoryBound(to: UInt8.self)
            for index in 0..<count {
                bytes[index] = UInt8(truncatingIfNeeded: index)
            }
            return true
        })
        XCTAssertTrue(storage.seal())

        let message = try storage.takeRawMessage()
        XCTAssertEqual(message.count, count)
        XCTAssertTrue(message.withUnsafeBytes { buffer in
            buffer.enumerated().allSatisfy { index, byte in
                byte == UInt8(truncatingIfNeeded: index)
            }
        })
    }

}
