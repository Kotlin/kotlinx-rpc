//
// Created by Johannes Zottele on 17.07.25.
//

#include "protowire.h"

#include <utility>

#include "src/google/protobuf/io/zero_copy_stream_impl_lite.h"
#include "src/google/protobuf/io/coded_stream.h"
#include "src/google/protobuf/wire_format_lite.h"

namespace pb = google::protobuf;
typedef pb::internal::WireFormatLite WireFormatLite;

namespace protowire {
class SinkStream final : public pb::io::CopyingOutputStream {
public:
    SinkStream(void *ctx, bool(*sink)(void *ctx, const void *buffer, int size))
        : ctx(ctx),
          sink(sink) {
    }

    bool Write(const void *buffer, int size) override {
        return sink(ctx, buffer, size);
    }

private:
    void *ctx;
    bool (*sink)(void *ctx, const void *buffer, int size);
};

class SourceStream final : public pb::io::ZeroCopyInputStream {
public:
    explicit SourceStream(const pw_zero_copy_input_t &input)
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
    pb::io::CopyingOutputStreamAdaptor cosa;
    pb::io::CodedOutputStream cos;

    explicit pw_encoder(protowire::SinkStream sink)
    : sinkStream(std::move(sink)),
      cosa(&sinkStream),
      cos(&cosa) {}

};

struct pw_decoder {
    protowire::SourceStream ss;
    pb::io::CodedInputStream cis;

    explicit pw_decoder(pw_zero_copy_input_t input)
    : ss(input),
      cis(&ss) {}
};


extern "C" {

    pw_string_t *pw_string_new(const char *str) {
        return new pw_string_t{str };
    }
    void pw_string_delete(pw_string_t *self) {
        delete self;
    }
    const char *pw_string_c_str(pw_string_t *self) {
        return self->str.c_str();
    }

    pw_encoder_t *pw_encoder_new(void* ctx, bool (* sink_fn)(void* ctx, const void* buf, int size)) {
        auto sink = protowire::SinkStream(ctx, sink_fn);
        return new pw_encoder(std::move(sink));
    }

    void pw_encoder_delete(pw_encoder_t *self) {
        delete self;
    }
    bool pw_encoder_flush(pw_encoder_t *self) {
        self->cos.Trim();
        if (!self->cosa.Flush()) {
            return false;
        }
        return !self->cos.HadError();
    }

    // check that there was no error
    static bool check(pw_encoder_t *self) {
        return !self->cos.HadError();
    }

#define WRITE_FIELD_FUNC( funcSuffix, wireTy, cTy) \
    bool pw_encoder_write_##funcSuffix(pw_encoder_t *self, int field_no, cTy value) { \
        WireFormatLite::Write##wireTy(field_no, value, &self->cos); \
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
    WRITE_FIELD_FUNC( enum, Enum, int)

    bool pw_encoder_write_string(pw_encoder_t *self, int field_no, pw_string_t *value) {
        WireFormatLite::WriteString(field_no, value->str, &self->cos);
        return check(self);
    }
    bool pw_encoder_write_bytes(pw_encoder_t *self, int field_no, pw_string_t *value) {
        WireFormatLite::WriteBytes(field_no, value->str, &self->cos);
        return check(self);
    }


    pw_decoder_t *pw_decoder_new(pw_zero_copy_input_t zero_copy_input) {
        return new pw_decoder_t(zero_copy_input);
    }

    void pw_decoder_delete(pw_decoder_t *self) {
        delete self;
    }

    uint32_t pw_decoder_read_tag(pw_decoder_t *self) {
        return self->cis.ReadTag();
    }

#define READ_VAL_FUNC( funcSuffix, wireTy, cTy) \
    bool pw_decoder_read_##funcSuffix(pw_decoder_t *self, cTy *value_ref) { \
        return WireFormatLite::ReadPrimitive<cTy, WireFormatLite::TYPE_##wireTy>(&self->cis, value_ref); \
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
    READ_VAL_FUNC( enum, ENUM, int)

    bool pw_decoder_read_string(pw_decoder_t *self, pw_string_t **string_ref) {
        *string_ref = new pw_string_t;
        return WireFormatLite::ReadString(&self->cis, &(*string_ref)->str);
    }
}
