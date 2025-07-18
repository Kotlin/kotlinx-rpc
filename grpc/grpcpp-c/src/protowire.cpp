//
// Created by Johannes Zottele on 17.07.25.
//

#include "protowire.h"

#include "src/google/protobuf/io/zero_copy_stream_impl_lite.h"
#include "src/google/protobuf/io/coded_stream.h"
#include "src/google/protobuf/wire_format_lite.h"

namespace pb = google::protobuf;

typedef pb::internal::WireFormatLite WireFormatLite;

struct pw_string {
    std::string str;
};

struct pw_encoder {
    pb::io::ArrayOutputStream aos;
    pb::io::CodedOutputStream cos;

    pw_encoder(uint8_t* buf, int size)
    : aos(buf, size),
      cos(&aos) {}

};

struct pw_decoder {
    pb::io::ArrayInputStream ais;
    pb::io::CodedInputStream cis;

    pw_decoder(uint8_t* buf, int size)
    : ais(buf, size),
      cis(&ais) {}
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

    pw_encoder_t * pw_encoder_new(uint8_t *buf, uint32_t cap) {
        return new pw_encoder_t(buf, cap);
    }

    void pw_encoder_delete(pw_encoder_t *self) {
        delete self;
    }

    // check if there was an error
    static bool check(pw_encoder_t *self) {
        return self->cos.HadError();
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


    pw_decoder_t *pw_decoder_new(uint8_t *buf, uint32_t cap) {
        return new pw_decoder_t(buf, cap);
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
