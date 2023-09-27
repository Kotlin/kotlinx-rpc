package org.jetbrains.krpc

import kotlinx.coroutines.runBlocking
import kotlin.test.Test

interface MainServiceTest : RPC, EmptyService {
    override suspend fun empty()
}

class MainTest {
    @Test
    fun test() = testServices<MainService, MainServiceTest, CommonService>()

    private inline fun <reified S1, reified S2, reified S3> testServices()
            where S1 : RPC, S2 : RPC, S3 : RPC,
                  S1 : EmptyService, S2 : EmptyService, S3 : EmptyService =
        runBlocking {
            testService<S1> { empty() }
            testService<S2> { empty() }
            testService<S3> { empty() }
        }
}
