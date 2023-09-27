package org.jetbrains.krpc

interface CommonTestService : RPC, EmptyService {
    override suspend fun empty()
}

abstract class CommonTestSuite {
    abstract fun runAsync(body: suspend () -> Unit)

    inline fun <reified S1, reified S2, reified S3, reified S4> testServices()
            where S1 : RPC, S2 : RPC, S3 : RPC, S4 : RPC,
                  S1 : EmptyService, S2 : EmptyService, S3 : EmptyService, S4 : EmptyService =
        runAsync {
            testService<S1> { empty() }
            testService<S2> { empty() }
            testService<S3> { empty() }
            testService<S4> { empty() }
        }
}
