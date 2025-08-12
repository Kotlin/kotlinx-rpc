//
// Created by Johannes Zottele on 11.07.25.
//

#include <grpcpp_c.h>

#include <memory>
#include <grpcpp/grpcpp.h>
#include <grpcpp/generic/generic_stub.h>
#include <grpcpp/impl/client_unary_call.h>
#include <google/protobuf/io/coded_stream.h>
#include <google/protobuf/io/zero_copy_stream_impl_lite.h>
#include "src/core/lib/iomgr/iomgr.h"

namespace pb = google::protobuf;

struct grpc_client {
    std::shared_ptr<grpc::Channel> channel;
    std::unique_ptr<grpc::GenericStub> stub;
};

struct grpc_method {
    std::string name_str;
    std::unique_ptr<grpc::internal::RpcMethod> method;
};

struct grpc_context {
    std::unique_ptr<grpc::ClientContext> context;
};

// struct grpc_channel {
//     std::shared_ptr<grpc::Channel> channel;
// };

extern "C" {

    grpc_client_t *grpc_client_create_insecure(const char *target) {
        std::string target_str = target;
        auto client = new grpc_client;
        client->channel = grpc::CreateChannel(target_str, grpc::InsecureChannelCredentials());
        client->stub = std::make_unique<grpc::GenericStub>(client->channel);
        return client;
    }

    void grpc_client_delete(const grpc_client_t *client) {
        delete client;
    }

    grpc_method_t *grpc_method_create(const char *method_name) {
        auto *method = new grpc_method;
        method->name_str = method_name;
        method->method = std::make_unique<grpc::internal::RpcMethod>(method->name_str.c_str(), grpc::internal::RpcMethod::NORMAL_RPC);
        return method;
    }

    void grpc_method_delete(const grpc_method_t *method) {
        delete method;
    }

    const char *grpc_method_name(const grpc_method_t *method) {
        return method->method->name();
    }

    grpc_context_t *grpc_context_create() {
        auto *context = new grpc_context;
        context->context = std::make_unique<grpc::ClientContext>();
        return context;
    }

    void grpc_context_delete(const grpc_context_t *context) {
        delete context;
    }

    static grpc_status_code_t status_to_c(grpc::StatusCode status);

    grpc_status_code_t grpc_client_call_unary_blocking(grpc_client_t *client, const char *method,
        grpc_slice req_slice, grpc_slice *resp_slice) {

        if (!client || !method) return GRPC_C_STATUS_INVALID_ARGUMENT;

        grpc::Slice cc_req_slice(req_slice, grpc::Slice::ADD_REF);
        grpc::ByteBuffer req_bb(&cc_req_slice, 1);

        grpc::ClientContext context;
        grpc::ByteBuffer resp_bb;

        const std::string method_path = "/Greeter/SayHello";
        grpc::internal::RpcMethod rpc(method_path.c_str(),
                                     grpc::internal::RpcMethod::NORMAL_RPC);

        grpc::Status st =
            grpc::internal::BlockingUnaryCall<grpc::ByteBuffer, grpc::ByteBuffer>(
                client->channel.get(), rpc, &context, req_bb, &resp_bb);


        if (!st.ok()) {
            // if not ok, no resp_buf is left null
            return status_to_c(st.error_code());
        }

        grpc::Slice cc_resp_slice;
        resp_bb.DumpToSingleSlice(&cc_resp_slice);
        *resp_slice = cc_resp_slice.c_slice();

        grpc::Slice test_slice(*resp_slice, grpc::Slice::ADD_REF);
        pb::io::ArrayInputStream  ais(test_slice.begin(), test_slice.size());
        pb::io::CodedInputStream  cis(&ais);


        cis.ReadTag();
        uint32_t id = 0;
        if (!cis.ReadVarint32(&id)) {
            std::cerr << "Failed to read id field\n";
        }

        return status_to_c(st.error_code());
    }

    void grpc_client_call_unary_callback(grpc_client_t *client, grpc_method_t *method, grpc_context_t *context,
                                         grpc_byte_buffer **req_buf, grpc_byte_buffer **resp_buf, void* callback_context, void (*callback)(grpc_status_code_t,void*)) {
        // the grpc::ByteBuffer representation is identical to (* grpc_byte_buffer) so we can safely cast it.
        // so a **grpc_byte_buffer can be cast to *grpc::ByteBuffer.
        static_assert(sizeof(grpc::ByteBuffer) == sizeof(grpc_byte_buffer*),
                      "ByteBuffer must have same representation as "
                      "grpc_byte_buffer*");
        const auto req_bb = reinterpret_cast<grpc::ByteBuffer *>(req_buf);
        const auto resp_bb = reinterpret_cast<grpc::ByteBuffer *>(resp_buf);
        grpc::internal::CallbackUnaryCall<grpc::ByteBuffer, grpc::ByteBuffer>(client->channel.get(), *method->method, context->context.get(), req_bb, resp_bb, [callback, callback_context](grpc::Status st) {
            const auto c_st = status_to_c(st.error_code());
            callback(c_st, callback_context);
        });
    }

    grpc_status_code_t status_to_c(grpc::StatusCode status) {
        switch (status) {
            case grpc::OK:
                return GRPC_C_STATUS_OK;
            case grpc::CANCELLED:
                return GRPC_C_STATUS_CANCELLED;
            case grpc::UNKNOWN:
                return GRPC_C_STATUS_UNKNOWN;
            case grpc::INVALID_ARGUMENT:
                return GRPC_C_STATUS_INVALID_ARGUMENT;
            case grpc::DEADLINE_EXCEEDED:
                return GRPC_C_STATUS_DEADLINE_EXCEEDED;
            case grpc::NOT_FOUND:
                return GRPC_C_STATUS_NOT_FOUND;
            case grpc::ALREADY_EXISTS:
                return GRPC_C_STATUS_ALREADY_EXISTS;
            case grpc::PERMISSION_DENIED:
                return GRPC_C_STATUS_PERMISSION_DENIED;
            case grpc::UNAUTHENTICATED:
                return GRPC_C_STATUS_UNAUTHENTICATED;
            case grpc::RESOURCE_EXHAUSTED:
                return GRPC_C_STATUS_RESOURCE_EXHAUSTED;
            case grpc::FAILED_PRECONDITION:
                return GRPC_C_STATUS_FAILED_PRECONDITION;
            case grpc::ABORTED:
                return GRPC_C_STATUS_ABORTED;
            case grpc::UNIMPLEMENTED:
                return GRPC_C_STATUS_UNIMPLEMENTED;
            case grpc::OUT_OF_RANGE:
                return GRPC_C_STATUS_OUT_OF_RANGE;
            case grpc::INTERNAL:
                return GRPC_C_STATUS_INTERNAL;
            case grpc::UNAVAILABLE:
                return GRPC_C_STATUS_UNAVAILABLE;
            case grpc::DATA_LOSS:
                return GRPC_C_STATUS_DATA_LOSS;
            case grpc::DO_NOT_USE:
                return GRPC_C_STATUS_DO_NOT_USE;
        }
    }


    uint32_t pb_decode_greeter_sayhello_response(grpc_slice response) {
        grpc::Slice cc_resp_slice(response, grpc::Slice::ADD_REF);
        pb::io::ArrayInputStream asi(cc_resp_slice.begin(), cc_resp_slice.size());
        pb::io::CodedInputStream cis(&asi);

        const auto tag = cis.ReadTag();
        if (tag != 8) {
            std::cerr << "Failed to read tag. Got: " << tag << std::endl;
        }

        uint32_t result;
        if (!cis.ReadVarint32(&result)) {
            std::cerr << "Failed to read result" << std::endl;
        } else {

        }
        return result;
    }


    grpc_status_code_t grpc_byte_buffer_dump_to_single_slice(grpc_byte_buffer *byte_buffer, grpc_slice *slice) {
        auto bb = reinterpret_cast<grpc::ByteBuffer*>(&byte_buffer);
        grpc::Slice cc_slice;
        bb->DumpToSingleSlice(&cc_slice);
        *slice = cc_slice.c_slice();
        return GRPC_C_STATUS_OK;
    }


    //// CHANNEL ////

    bool kgrpc_iomgr_run_in_background() {
        return grpc_iomgr_run_in_background();
    }

}


