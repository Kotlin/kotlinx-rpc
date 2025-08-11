//
// Created by Johannes Zottele on 11.07.25.
//

#ifndef GRPCPP_C_H
#define GRPCPP_C_H

#include <stdint.h>
#include <grpc/slice.h>
#include <grpc/byte_buffer.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct grpc_client grpc_client_t;
typedef struct grpc_method grpc_method_t;
typedef struct grpc_context grpc_context_t;

typedef enum StatusCode {
  GRPC_C_STATUS_OK = 0,
  GRPC_C_STATUS_CANCELLED = 1,
  GRPC_C_STATUS_UNKNOWN = 2,
  GRPC_C_STATUS_INVALID_ARGUMENT = 3,
  GRPC_C_STATUS_DEADLINE_EXCEEDED = 4,
  GRPC_C_STATUS_NOT_FOUND = 5,
  GRPC_C_STATUS_ALREADY_EXISTS = 6,
  GRPC_C_STATUS_PERMISSION_DENIED = 7,
  GRPC_C_STATUS_UNAUTHENTICATED = 16,
  GRPC_C_STATUS_RESOURCE_EXHAUSTED = 8,
  GRPC_C_STATUS_FAILED_PRECONDITION = 9,
  GRPC_C_STATUS_ABORTED = 10,
  GRPC_C_STATUS_OUT_OF_RANGE = 11,
  GRPC_C_STATUS_UNIMPLEMENTED = 12,
  GRPC_C_STATUS_INTERNAL = 13,
  GRPC_C_STATUS_UNAVAILABLE = 14,
  GRPC_C_STATUS_DATA_LOSS = 15,
  GRPC_C_STATUS_DO_NOT_USE = -1
} grpc_status_code_t;

grpc_client_t *grpc_client_create_insecure(const char *target);
void grpc_client_delete(const grpc_client_t *client);

grpc_method_t *grpc_method_create(const char *method_name);
void grpc_method_delete(const grpc_method_t *method);

const char *grpc_method_name(const grpc_method_t *method);

grpc_context_t *grpc_context_create();
void grpc_context_delete(const grpc_context_t *context);

grpc_status_code_t grpc_client_call_unary_blocking(grpc_client_t *client, const char *method,
        grpc_slice req_slice, grpc_slice *resp_slice);

void grpc_client_call_unary_callback(grpc_client_t *client, grpc_method_t *method, grpc_context_t *context,
                                     grpc_byte_buffer **req_buf, grpc_byte_buffer **resp_buf, void* callback_context, void (*callback)(grpc_status_code_t,void*));

uint32_t pb_decode_greeter_sayhello_response(grpc_slice response);

grpc_status_code_t grpc_byte_buffer_dump_to_single_slice(grpc_byte_buffer *byte_buffer, grpc_slice *slice);

#ifdef __cplusplus
    }
#endif

#endif //GRPCPP_C_H
