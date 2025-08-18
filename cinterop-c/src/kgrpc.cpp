/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

#include <kgrpc.h>

#include "src/core/lib/iomgr/iomgr.h"

extern "C" {

    bool kgrpc_iomgr_run_in_background() {
        return grpc_iomgr_run_in_background();
    }

}


