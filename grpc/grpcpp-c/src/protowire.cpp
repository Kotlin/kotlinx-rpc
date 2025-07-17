//
// Created by Johannes Zottele on 17.07.25.
//

#include "protowire.h"

#include "src/google/protobuf/io/zero_copy_stream_impl_lite.h"
#include "src/google/protobuf/io/coded_stream.h"
#include "src/google/protobuf/wire_format_lite.h"

namespace pb = google::protobuf;

typedef pb::internal::WireFormatLite WireFormatLite;

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

    bool pw_encoder_write_bool(pw_encoder_t *self, int field_no, bool v) {
        WireFormatLite::WriteBool(field_no, v, &self->cos);
        return check(self);
    }

    bool pw_encoder_write_string(pw_encoder_t *self, int field_no, const char *v) {
        // TODO: This requires a copy of the string.
        //  We could write the raw buffer manually to avoid this.
        const std::string str(v);
        WireFormatLite::WriteString(field_no, str, &self->cos);
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

    bool pw_decoder_read_bool(pw_decoder_t *self, bool *v) {
        return WireFormatLite::ReadPrimitive<bool, WireFormatLite::TYPE_BOOL>(&self->cis, v);
    }

    bool pw_decoder_read_string(pw_decoder_t *self, void **opaque_string, const char **out) {
        // create a std::string object and place the pointer to the opaque_string ptr references
        // this string object will outlive the functions, the caller is responsible to call
        // pw_decoder_delete_opaque_string with the opaque string pointer
        auto *str_ptr = new std::string;
        *opaque_string = str_ptr;
        const auto ok = WireFormatLite::ReadString(&self->cis, str_ptr);
        // set the c_str start pointer to the out ptr reference
        *out = str_ptr->c_str();
        return ok;
    }

    void pw_decoder_delete_opaque_string(void *opaque_string) {
        auto *str_ptr = static_cast<std::string *>(opaque_string);
        delete str_ptr;
    }
}
