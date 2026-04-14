/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

#include "protowire.h"

#include <utility>

#include "src/google/protobuf/io/zero_copy_stream_impl_lite.h"
#include "src/google/protobuf/io/coded_stream.h"
#include "src/google/protobuf/wire_format_lite.h"

namespace pb = google::protobuf;
typedef pb::internal::WireFormatLite WireFormatLite;

namespace protowire {
    /**
     * A bridge that passes write calls to the K/N side, to write the data into a kotlinx.io.Sink.
     *
     * This reduces the amount of copying, as the callback on the K/N may directly use the
     * buffer pointer to copy the whole chunk at into the stream.
     */
    class SinkStream final : public pb::io::CopyingOutputStream {
    public:
        /**
         * Constructs the stream with a ctx pointer and a callback to the K/N side.
         * The ctx pointer is used to on the K/N to reference a Kotlin managed object
         * from within its static callback function.
         *
         * @param thisRef the context used by the K/N side to reference Kotlin managed objects.
         * @param sink_fn the K/N callback to write data into the sink
         */
        SinkStream(void *thisRef, bool(*sink_fn)(void *ctx, const void *buffer, int size))
            : ctx(thisRef),
              sink_fn(sink_fn) {
        }

        bool Write(const void *buffer, int size) override {
            return sink_fn(ctx, buffer, size);
        }

    private:
        void *ctx;
        bool (*sink_fn)(void *ctx, const void *buffer, int size);
    };

    /**
     * A bridge that passes read calls to the K/N side, to read data from a kotlinx.io.Buffer.
     *
     * This allows efficient data reading from the K/N side buffer, as it allows
     * directly accessing continuous memory blocks from within the buffer, instead of copying them
     * via C-Interop.
     *
     * All ZeroCopyInputStream methods are delegated to the K/N call back functions, hold in
     * the pw_zero_copy_input_t.
     */
    class BufferSourceStream final : public pb::io::ZeroCopyInputStream {
    public:
        /**
         * Constructs the BufferSourceStream to access kotlinx.io.Buffer segments directly, without
         * copying them via C-Interop.
         *
         * @param input a struct containing K/N callbacks for all methods of the ZeroCopyInputStream.
         */
        explicit BufferSourceStream(const pw_zero_copy_input_t &input)
            : input(input) {
        }

        bool Next(const void **data, int *size) override {
            auto result = input.next(input.ctx, data, size);
            return result;
        };

        void BackUp(int count) override {
            return input.backUp(input.ctx, count);
        };

        bool Skip(int count) override {
            return input.skip(input.ctx, count);
        };

        int64_t ByteCount() const override {
            return input.byteCount(input.ctx);
        };

    private:
        pw_zero_copy_input_t input;
    };

}

struct pw_string {
    std::string str;
};

struct pw_encoder {
    protowire::SinkStream sinkStream;
    pb::io::CopyingOutputStreamAdaptor copyingOutputStreamAdaptor;
    pb::io::CodedOutputStream codedOutputStream;

    explicit pw_encoder(protowire::SinkStream sink)
    : sinkStream(std::move(sink)),
      copyingOutputStreamAdaptor(&sinkStream),
      codedOutputStream(&copyingOutputStreamAdaptor) {
        codedOutputStream.EnableAliasing(true);
    }
};

struct pw_decoder {
    protowire::BufferSourceStream bufferSourceStream;
    pb::io::CodedInputStream codedInputStream;

    explicit pw_decoder(pw_zero_copy_input_t input)
    : bufferSourceStream(input),
      codedInputStream(&bufferSourceStream) {}
};


extern "C" {

    /// STD::STRING WRAPPER IMPLEMENTATION ///

    pw_string_t *pw_string_new(const char *str) {
        return new pw_string_t{str };
    }
    void pw_string_delete(pw_string_t *self) {
        delete self;
    }
    const char *pw_string_c_str(pw_string_t *self) {
        return self->str.c_str();
    }

    /// ENCODER IMPLEMENTATION ///

    pw_encoder_t *pw_encoder_new(void* ctx, bool (* sink_fn)(void* ctx, const void* buf, int size)) {
        auto sink = protowire::SinkStream(ctx, sink_fn);
        return new pw_encoder(std::move(sink));
    }

    void pw_encoder_delete(pw_encoder_t *self) {
        delete self;
    }
    bool pw_encoder_flush(pw_encoder_t *self) {
        self->codedOutputStream.Trim();
        if (!self->copyingOutputStreamAdaptor.Flush()) {
            return false;
        }
        return !self->codedOutputStream.HadError();
    }

    // check that there was no error
    static bool check(pw_encoder_t *self) {
        return !self->codedOutputStream.HadError();
    }

#define WRITE_FIELD_FUNC( funcSuffix, wireTy, cTy) \
    bool pw_encoder_write_##funcSuffix(pw_encoder_t *self, int field_no, cTy value) { \
        WireFormatLite::Write##wireTy(field_no, value, &self->codedOutputStream); \
        return check(self); \
    }

    WRITE_FIELD_FUNC( bool, Bool, bool)
    WRITE_FIELD_FUNC( int32, Int32, int32_t)
    WRITE_FIELD_FUNC( int64, Int64, int64_t)
    WRITE_FIELD_FUNC( uint32, UInt32, uint32_t)
    WRITE_FIELD_FUNC( uint64, UInt64, uint64_t)
    WRITE_FIELD_FUNC( sint32, SInt32, int32_t)
    WRITE_FIELD_FUNC( sint64, SInt64, int64_t)
    WRITE_FIELD_FUNC( fixed32, Fixed32, uint32_t)
    WRITE_FIELD_FUNC( fixed64, Fixed64, uint64_t)
    WRITE_FIELD_FUNC( sfixed32, SFixed32, int32_t)
    WRITE_FIELD_FUNC( sfixed64, SFixed64, int64_t)
    WRITE_FIELD_FUNC( float, Float, float)
    WRITE_FIELD_FUNC( double, Double, double)
    WRITE_FIELD_FUNC( enum, Enum, int)

    bool pw_encoder_write_string(pw_encoder_t *self, int field_no, const char *data, int size) {
        return pw_encoder_write_bytes(self, field_no, data, size);
    }
    bool pw_encoder_write_bytes(pw_encoder_t *self, int field_no, const void *data, int size) {
        WireFormatLite::WriteTag(field_no, WireFormatLite::WIRETYPE_LENGTH_DELIMITED, &self->codedOutputStream);
        self->codedOutputStream.WriteVarint32(size);
        self->codedOutputStream.WriteRawMaybeAliased(data, size);
        return check(self);
    }

    bool pw_encoder_write_tag(pw_encoder_t *self, int field_no, int wire_type) {
        WireFormatLite::WriteTag(field_no, static_cast<WireFormatLite::WireType>(wire_type), &self->codedOutputStream);
        return check(self);
    }

#define WRITE_FIELD_NO_TAG( funcSuffix, wireTy, cTy) \
    bool pw_encoder_write_##funcSuffix##_no_tag(pw_encoder_t *self, cTy value) { \
        WireFormatLite::Write##wireTy##NoTag(value, &self->codedOutputStream); \
        return check(self); \
    }

    WRITE_FIELD_NO_TAG( bool, Bool, bool)
    WRITE_FIELD_NO_TAG( int32, Int32, int32_t)
    WRITE_FIELD_NO_TAG( int64, Int64, int64_t)
    WRITE_FIELD_NO_TAG( uint32, UInt32, uint32_t)
    WRITE_FIELD_NO_TAG( uint64, UInt64, uint64_t)
    WRITE_FIELD_NO_TAG( sint32, SInt32, int32_t)
    WRITE_FIELD_NO_TAG( sint64, SInt64, int64_t)
    WRITE_FIELD_NO_TAG( fixed32, Fixed32, uint32_t)
    WRITE_FIELD_NO_TAG( fixed64, Fixed64, uint64_t)
    WRITE_FIELD_NO_TAG( sfixed32, SFixed32, int32_t)
    WRITE_FIELD_NO_TAG( sfixed64, SFixed64, int64_t)
    WRITE_FIELD_NO_TAG( float, Float, float)
    WRITE_FIELD_NO_TAG( double, Double, double)
    WRITE_FIELD_NO_TAG( enum, Enum, int)

    bool pw_encoder_write_raw_bytes(pw_encoder_t *self, const void *data, int size) {
        self->codedOutputStream.WriteRawMaybeAliased(data, size);
        return check(self);
    }

    /// DECODER IMPLEMENTATION ///

    pw_decoder_t *pw_decoder_new(pw_zero_copy_input_t zero_copy_input) {
        return new pw_decoder_t(zero_copy_input);
    }

    void pw_decoder_delete(pw_decoder_t *self) {
        delete self;
    }

    void pw_decoder_close(pw_decoder_t *self) {
        // the deconstructor backs the stream up to the current position.
        self->codedInputStream.~CodedInputStream();
    }

    uint32_t pw_decoder_read_tag(pw_decoder_t *self) {
        return self->codedInputStream.ReadTag();
    }

    int pw_decoder_read_validated_tag(pw_decoder_t *self, uint32_t *tag_out) {
        // Sub-message boundary: BytesUntilLimit() == 0 when a PushLimit scope
        // is active and all bytes within it have been consumed. This mirrors
        // ReadTag()'s internal limit check.
        if (self->codedInputStream.BytesUntilLimit() == 0) {
            return 0;
        }

        // Read the first byte via ReadRaw to reliably distinguish top-level
        // EOF from a varint start. ReadRaw(1) fails only when the stream is
        // genuinely exhausted (BytesUntilLimit > 0 was checked above, so a
        // limit boundary is not the cause). This avoids ReadTag()'s ambiguous
        // return-0-for-both-EOF-and-errors and ConsumedEntireMessage()'s
        // broken behavior at the top level (no limit set → legitimate_message_end_
        // is never set).
        uint8_t b;
        if (!self->codedInputStream.ReadRaw(&b, 1)) {
            return 0; // top-level EOF
        }

        // Parse the varint manually, tracking byte count for overlong detection.
        // Tags are uint32 (max 5 varint bytes), but we must read up to 10 bytes
        // to detect the >10-byte varint conformance case.
        uint64_t result = b & 0x7F;
        int bytes_used = 1;

        while (b >= 0x80) {
            if (bytes_used >= 10) {
                return -1; // varint exceeds 10 bytes
            }
            if (!self->codedInputStream.ReadRaw(&b, 1)) {
                return -1; // truncated varint
            }
            result |= static_cast<uint64_t>(b & 0x7F) << (7 * bytes_used);
            bytes_used++;
        }

        if (result == 0) return -1;         // zero tag (invalid field number 0)
        if (result > UINT32_MAX) return -1;  // exceeds 32-bit tag range

        // Reject overlong varint encoding: the varint used more bytes than the
        // minimum required for its value. Each varint byte carries 7 payload bits.
        int min_bytes;
        if      (result < (1ULL <<  7)) min_bytes = 1;
        else if (result < (1ULL << 14)) min_bytes = 2;
        else if (result < (1ULL << 21)) min_bytes = 3;
        else if (result < (1ULL << 28)) min_bytes = 4;
        else                            min_bytes = 5;

        if (bytes_used > min_bytes) return -1;

        *tag_out = static_cast<uint32_t>(result);
        return 1;
    }

    bool pw_decoder_consumed_entire_msg(pw_decoder_t *self) {
        return self->codedInputStream.ConsumedEntireMessage();
    }

#define READ_VAL_FUNC( funcSuffix, wireTy, cTy) \
    bool pw_decoder_read_##funcSuffix(pw_decoder_t *self, cTy *value_ref) { \
        return WireFormatLite::ReadPrimitive<cTy, WireFormatLite::TYPE_##wireTy>(&self->codedInputStream, value_ref); \
    }

    READ_VAL_FUNC( bool, BOOL, bool)
    READ_VAL_FUNC( int32, INT32, int32_t)
    READ_VAL_FUNC( int64, INT64, int64_t)
    READ_VAL_FUNC( uint32, UINT32, uint32_t)
    READ_VAL_FUNC( uint64, UINT64, uint64_t)
    READ_VAL_FUNC( sint32, SINT32, int32_t)
    READ_VAL_FUNC( sint64, SINT64, int64_t)
    READ_VAL_FUNC( fixed32, FIXED32, uint32_t)
    READ_VAL_FUNC( fixed64, FIXED64, uint64_t)
    READ_VAL_FUNC( sfixed32, SFIXED32, int32_t)
    READ_VAL_FUNC( sfixed64, SFIXED64, int64_t)
    READ_VAL_FUNC( float, FLOAT, float)
    READ_VAL_FUNC( double, DOUBLE, double)
    READ_VAL_FUNC( enum, ENUM, int)

    bool pw_decoder_read_string(pw_decoder_t *self, pw_string_t **string_ref) {
        *string_ref = new pw_string_t;
        return WireFormatLite::ReadString(&self->codedInputStream, &(*string_ref)->str);
    }

    /// Validates that [data] of length [len] is structurally valid UTF-8 per RFC 3629.
    /// Rejects overlong encodings, surrogates (U+D800..U+DFFF), and code points above U+10FFFF.
    static bool is_structurally_valid_utf8(const char *data, size_t len) {
        auto *bytes = reinterpret_cast<const unsigned char *>(data);
        size_t i = 0;
        while (i < len) {
            unsigned char b0 = bytes[i];
            if (b0 <= 0x7F) {
                // 1-byte (ASCII)
                i++;
            } else if (b0 >= 0xC2 && b0 <= 0xDF) {
                // 2-byte: 110xxxxx 10xxxxxx (U+0080..U+07FF)
                if (i + 1 >= len) return false;
                if ((bytes[i + 1] & 0xC0) != 0x80) return false;
                i += 2;
            } else if (b0 >= 0xE0 && b0 <= 0xEF) {
                // 3-byte: 1110xxxx 10xxxxxx 10xxxxxx (U+0800..U+FFFF, excluding surrogates)
                if (i + 2 >= len) return false;
                unsigned char b1 = bytes[i + 1], b2 = bytes[i + 2];
                if ((b1 & 0xC0) != 0x80 || (b2 & 0xC0) != 0x80) return false;
                if (b0 == 0xE0 && b1 < 0xA0) return false;  // overlong
                if (b0 == 0xED && b1 >= 0xA0) return false;  // surrogates
                i += 3;
            } else if (b0 >= 0xF0 && b0 <= 0xF4) {
                // 4-byte: 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx (U+10000..U+10FFFF)
                if (i + 3 >= len) return false;
                unsigned char b1 = bytes[i + 1], b2 = bytes[i + 2], b3 = bytes[i + 3];
                if ((b1 & 0xC0) != 0x80 || (b2 & 0xC0) != 0x80 || (b3 & 0xC0) != 0x80) return false;
                if (b0 == 0xF0 && b1 < 0x90) return false;   // overlong
                if (b0 == 0xF4 && b1 >= 0x90) return false;   // above U+10FFFF
                i += 4;
            } else {
                // Invalid lead byte (0x80..0xBF, 0xC0..0xC1, 0xF5..0xFF)
                return false;
            }
        }
        return true;
    }

    bool pw_string_is_valid_utf8(pw_string_t *self) {
        return is_structurally_valid_utf8(self->str.data(), self->str.size());
    }

    bool pw_decoder_read_raw_bytes(pw_decoder_t *self, void* buffer, int size) {
        return self->codedInputStream.ReadRaw(buffer, size);
    }

    int pw_decoder_push_limit(pw_decoder_t *self, int limit) {
        return self->codedInputStream.PushLimit(limit);
    }

    void pw_decoder_pop_limit(pw_decoder_t *self, int limit) {
        self->codedInputStream.PopLimit(limit);
    }

    int pw_decoder_bytes_until_limit(pw_decoder_t *self) {
        return self->codedInputStream.BytesUntilLimit();
    }

    uint32_t pw_size_int32(int32_t value) {
        return WireFormatLite::Int32Size(value);
    }
    uint32_t pw_size_int64(int64_t value) {
        return WireFormatLite::Int64Size(value);
    }
    uint32_t pw_size_uint32(uint32_t value) {
        return WireFormatLite::UInt32Size(value);
    }
    uint32_t pw_size_uint64(uint64_t value) {
        return WireFormatLite::UInt64Size(value);
    }
    uint32_t pw_size_sint32(int32_t value) {
        return WireFormatLite::SInt32Size(value);
    }
    uint32_t pw_size_sint64(int64_t value) {
        return WireFormatLite::SInt64Size(value);
    }

}
