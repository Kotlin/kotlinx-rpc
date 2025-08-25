// Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

#include <kgrpc.h>

extern "C" {


    void test() {
        grpc_slice test = grpc_slice_from_static_string("test");
        grpc_slice_unref(test);
    }

}


