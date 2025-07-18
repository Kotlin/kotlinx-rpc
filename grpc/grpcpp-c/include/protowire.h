#ifndef PROTOWIRE_H
#define PROTOWIRE_H

#include <stdint.h>
#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

    typedef struct pw_string pw_string_t;

    pw_string_t * pw_string_new(const char *str);
    void pw_string_delete(pw_string_t *self);
    const char * pw_string_c_str(pw_string_t *self);


    //// WIRE ENCODER ////

    typedef struct pw_encoder pw_encoder_t;

    pw_encoder_t * pw_encoder_new(uint8_t *buf, uint32_t cap);
    void pw_encoder_delete(pw_encoder_t *self);

    bool pw_encoder_write_bool(pw_encoder_t *self, int field_no, bool value);
    bool pw_encoder_write_int32(pw_encoder_t *self, int field_no, int32_t value);
    bool pw_encoder_write_int64(pw_encoder_t *self, int field_no, int64_t value);
    bool pw_encoder_write_uint32(pw_encoder_t *self, int field_no, uint32_t value);
    bool pw_encoder_write_uint64(pw_encoder_t *self, int field_no, uint64_t value);
    bool pw_encoder_write_sint32(pw_encoder_t *self, int field_no, int32_t value);
    bool pw_encoder_write_sint64(pw_encoder_t *self, int field_no, int64_t value);
    bool pw_encoder_write_fixed32(pw_encoder_t *self, int field_no, uint32_t value);
    bool pw_encoder_write_fixed64(pw_encoder_t *self, int field_no, uint64_t value);
    bool pw_encoder_write_sfixed32(pw_encoder_t *self, int field_no, int32_t value);
    bool pw_encoder_write_sfixed64(pw_encoder_t *self, int field_no, int64_t value);
    bool pw_encoder_write_float(pw_encoder_t *self, int field_no, float value);
    bool pw_encoder_write_double(pw_encoder_t *self, int field_no, double value);
    bool pw_encoder_write_enum(pw_encoder_t *self, int field_no, int value);
    bool pw_encoder_write_string(pw_encoder_t *self, int field_no, pw_string_t *value);
    bool pw_encoder_write_bytes(pw_encoder_t *self, int field_no, pw_string_t *value);



    //// WIRE DECODER ////

    typedef struct pw_decoder pw_decoder_t;

    pw_decoder_t * pw_decoder_new(uint8_t *buf, uint32_t cap);
    void pw_decoder_delete(pw_decoder_t *self);

    uint32_t pw_decoder_read_tag(pw_decoder_t *self);

    /** Read primitive **/

    bool pw_decoder_read_bool(pw_decoder_t *self, bool *value);
    bool pw_decoder_read_int32(pw_decoder_t *self, int32_t *value);
    bool pw_decoder_read_int64(pw_decoder_t *self, int64_t *value);
    bool pw_decoder_read_uint32(pw_decoder_t *self, uint32_t *value);
    bool pw_decoder_read_uint64(pw_decoder_t *self, uint64_t *value);
    bool pw_decoder_read_sint32(pw_decoder_t *self, int32_t *value);
    bool pw_decoder_read_sint64(pw_decoder_t *self, int64_t *value);
    bool pw_decoder_read_fixed32(pw_decoder_t *self, uint32_t *value);
    bool pw_decoder_read_fixed64(pw_decoder_t *self, uint64_t *value);
    bool pw_decoder_read_sfixed32(pw_decoder_t *self, int32_t *value);
    bool pw_decoder_read_sfixed64(pw_decoder_t *self, int64_t *value);
    bool pw_decoder_read_float(pw_decoder_t *self, float *value);
    bool pw_decoder_read_double(pw_decoder_t *self, double *value);
    bool pw_decoder_read_enum(pw_decoder_t *self, int *value);
    bool pw_decoder_read_string(pw_decoder_t *self, pw_string_t **opaque_string);

#ifdef __cplusplus
    }
#endif

#endif //PROTOWIRE_H
