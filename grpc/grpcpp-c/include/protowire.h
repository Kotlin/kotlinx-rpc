#ifndef PROTOWIRE_H
#define PROTOWIRE_H

#include <stdint.h>
#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

    //// WIRE ENCODER ////

    typedef struct pw_encoder pw_encoder_t;

    pw_encoder_t * pw_encoder_new(uint8_t *buf, uint32_t cap);
    void pw_encoder_delete(pw_encoder_t *self);

    bool pw_encoder_write_bool(pw_encoder_t *self, int field_no, bool v);

    bool pw_encoder_write_string(pw_encoder_t *self, int field_no, const char *v);


    //// WIRE DECODER ////

    typedef struct pw_decoder pw_decoder_t;

    pw_decoder_t * pw_decoder_new(uint8_t *buf, uint32_t cap);
    void pw_decoder_delete(pw_decoder_t *self);

    uint32_t pw_decoder_read_tag(pw_decoder_t *self);
    bool pw_decoder_read_bool(pw_decoder_t *self, bool *v);
    bool pw_decoder_read_string(pw_decoder_t *self, void **opaque_string, const char **out);
    void pw_decoder_delete_opaque_string(void *opaque_string);

#ifdef __cplusplus
    }
#endif

#endif //PROTOWIRE_H
