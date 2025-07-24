#ifndef PROTOWIRE_H
#define PROTOWIRE_H

#include <stdint.h>
#include <stdbool.h>

// Defines the C wrapper around the C++ Wire Format encoding/decoding API
// (WireFormatLite, Coded(Input|Output)Stream, ZeroCopyInputStream, CopingOutputStream)
#ifdef __cplusplus
extern "C" {
#endif

    //// STD::STRING WRAPPER ////

    // A std::string wrapper that helps reduce copies when C++ api returns std::strings
    typedef struct pw_string pw_string_t;

    pw_string_t * pw_string_new(const char *str);
    void pw_string_delete(pw_string_t *self);
    const char * pw_string_c_str(pw_string_t *self);


    //// WIRE ENCODER ////

    typedef struct pw_encoder pw_encoder_t;

    /**
     * Create a new pw_encoder_t that wraps a CodedOutputStream to encode values into a wire format stream.
     *
     * @param ctx a stable pointer to a Kotlin managed object, used by the K/N sink callback to access Kotlin objects.
     * @param sink_fn the K/N callback function to write encoded data into the kotlinx.io.Sink.
     */
    pw_encoder_t *pw_encoder_new(void* ctx, bool (*sink_fn)(void* ctx, const void* buf, int size));
    void pw_encoder_delete(pw_encoder_t *self);
    bool pw_encoder_flush(pw_encoder_t *self);

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
    bool pw_encoder_write_string(pw_encoder_t *self, int field_no, const char *data, int size);
    bool pw_encoder_write_bytes(pw_encoder_t *self, int field_no, const void *data, int size);


    //// WIRE DECODER ////

    typedef struct pw_decoder pw_decoder_t;

    /**
     * Holds callbacks corresponding to the methods of a ZeroCopyInputStream.
     * They are called to retrieve data from the K/N side with a minimal number of copies.
     *
     * For method documentation see the ZeroCopyInputStream (C++) interface and the ZeroCopyInputSource (Kotlin) class.
     */
    typedef struct pw_zero_copy_input {
        void *ctx;
        bool (*next)(void *ctx, const void **data, int *size);
        void (*backUp)(void *ctx, int size);
        bool (*skip)(void *ctx, int size);
        int64_t (*byteCount)(void *ctx);
    } pw_zero_copy_input_t;


    /**
     * Create a new pw_decoder_t that wraps a CodedInputStream to decode values from a wire format stream.
     *
     * @param zero_copy_input holds callbacks to the K/N side, matching the ZeroCopyInputStream interface.
     */
    pw_decoder_t * pw_decoder_new(pw_zero_copy_input_t zero_copy_input);
    void pw_decoder_delete(pw_decoder_t *self);

    uint32_t pw_decoder_read_tag(pw_decoder_t *self);
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
    // To read an actual bytes field, you must combine read_int32 and this function
    bool pw_decoder_read_raw_bytes(pw_decoder_t *self, void* buffer, int size);


#ifdef __cplusplus
    }
#endif

#endif //PROTOWIRE_H
