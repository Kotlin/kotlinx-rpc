package org.jetbrains.krpc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface JsService : RPC, EmptyService {
    override suspend fun empty()
}

fun main() {
    CoroutineScope(Dispatchers.Main).launch {
        testService<JsService> { empty() }
        testService<CommonService> { empty() }
    }
}
