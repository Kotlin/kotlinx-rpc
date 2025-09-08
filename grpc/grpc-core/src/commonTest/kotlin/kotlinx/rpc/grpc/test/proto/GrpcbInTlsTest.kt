/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.proto

import hello.HelloRequest
import hello.HelloService
import hello.invoke
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.grpc.GrpcClient
import kotlinx.rpc.grpc.GrpcServer
import kotlinx.rpc.grpc.TlsChannelCredentialsBuilder
import kotlinx.rpc.grpc.TlsServerCredentialsBuilder
import kotlinx.rpc.grpc.test.EchoRequest
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.EchoServiceImpl
import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.test.Test

private const val PORT = 50051

class GrpcbInTlsTest {


    @Test
    fun testTlsCall() = runTest {
        val grpcClient = GrpcClient("grpcb.in", 9001)
        val service = grpcClient.withService<HelloService>()
        val request = HelloRequest {
            greeting = "Postman"
        }
        val result = service.SayHello(request)

        println(result.reply)
    }


    @Test
    fun testLocalTls() = runTest {
        val serverTls = TlsServerCredentialsBuilder()
            .keyManager(SERVER_CERT_PEM, SERVER_KEY_PEM)
            .build()

        val grpcServer = GrpcServer(
            PORT,
            credentials = serverTls,
            builder = {
                registerService<EchoService> { EchoServiceImpl() }
            })
        grpcServer.start()

        val clientTls = TlsChannelCredentialsBuilder()
            .trustManager(SERVER_CERT_PEM)
            .build()

        val grpcClient = GrpcClient(
            "localhost", PORT,
            credentials = clientTls,
        ) {}

        val service = grpcClient.withService<EchoService>()
        val request = EchoRequest {
            message = "Postman"
        }

        try {
            service.UnaryEcho(request)
        } catch (t: Throwable) {
            println("[DEBUG_LOG] TLS test failed: ${t::class.simpleName}: ${t.message}")
            t.printStackTrace()
            throw t
        } finally {
            grpcServer.shutdown()
            grpcServer.awaitTermination()
            grpcClient.shutdown()
            grpcClient.awaitTermination()
        }
    }


    private val SERVER_CERT_PEM = """
    -----BEGIN CERTIFICATE-----
    MIIFfTCCA2WgAwIBAgIUOfRdPPo6IDmMJKBumLdSe59ldxEwDQYJKoZIhvcNAQEL
    BQAwTjELMAkGA1UEBhMCVVMxDjAMBgNVBAgMBVN0YXRlMQ0wCwYDVQQHDARDaXR5
    MQwwCgYDVQQKDANEZXYxEjAQBgNVBAMMCWxvY2FsaG9zdDAeFw0yNTA5MDgxMzIx
    MTNaFw0yNjA5MDgxMzIxMTNaME4xCzAJBgNVBAYTAlVTMQ4wDAYDVQQIDAVTdGF0
    ZTENMAsGA1UEBwwEQ2l0eTEMMAoGA1UECgwDRGV2MRIwEAYDVQQDDAlsb2NhbGhv
    c3QwggIiMA0GCSqGSIb3DQEBAQUAA4ICDwAwggIKAoICAQDCFj6DIg8qZm26Qque
    6kyVADZgJrBgL19HNgLmDpxixMlnSIOjWMb2IpAcT7Ln6PytEevoyPFubO0uXcRT
    jyf3xIaXWjj299w0TsvGYF6rZhUApKIYAKiamIjZLid0+VaKJzGzoIDFAU0W1gsp
    x9sz0YkP0OHuMcOjnkxIbqgL/lZOg1JDVf6hJrAi6CE0Iar03/R6cj+GNsf6W3RF
    MSUh9MWzDhgVY5DcVLf44V9s8hOiHu8p46aVirHti72pGbfYFNfD23Xmv14QX9pY
    qtuaxDWcMk44K48kf+7ztC3jDbzJkloy0oFEWSmeUwVNYltkrG3Z8753y6ZcHBBc
    dw+lq3Fd0x3/9gSKs3w2zjzetym6JrKC5wzItuAqe5rJibAjB3aPhjtKKHmVXzSC
    mjuRWtxk/yL7V8yPA6FelwBLgVXaZAUL/IsabkHOerJsS7tZbM4/1baZIbfiVhIJ
    wwXHri5zQPTiX/tWZGwjic0ZlxVhF0D2qnILwhXdQyze3K1aoV3T8+i2QIpiLmWQ
    jCmqzoM55r/5E2KO/dDbfB8Qtw/rd08wOUcl9+bphetw1vyka4SGSZBwpGy6AUvC
    RAGA+5Hk0eqHW6Zqqr7MLzeLd1OlixVLhKgSGRDp7kdRlhPeY9kGrEGU/hXrfVKI
    YxbdL5JvK838jfNgKeurWYL8OQIDAQABo1MwUTAdBgNVHQ4EFgQUtOl1Hhw8PeHN
    hXC5h4Q9mD0oFdwwHwYDVR0jBBgwFoAUtOl1Hhw8PeHNhXC5h4Q9mD0oFdwwDwYD
    VR0TAQH/BAUwAwEB/zANBgkqhkiG9w0BAQsFAAOCAgEAp4jaSnXHxTBzSopi8tOa
    utxTV45Cz7dhUCfvgPPVsYURgg4MZMLJ908awcU0Wge0DbnwGQx+BQnsh8YCbroG
    LVPzbhl4xUmroDfr4XX8lVWmfhBOSC8K1ZR1s6udnHYkfzzZ4w5oBBfVJGsbHrNu
    KdYsHvSMpnneRBkMHwjHggcWpJX/Fg+c1lg1WP938qoCgPFZAgVEry5OdgMK0bmj
    OUMrM1WKYq/CMi6t0+iwX7V+sRe8+gD+clIJXdg2j9bN+kHXw5FnVBLvvdzED1tL
    FG5M+Uq10bomxpLbsUYX6+7ZIkCLvkhMovSYI/M/TcNzy8i41CxS+UibtObJ7XRZ
    512650ySuBKRSwfk8MCp7LOL+nosxcLHtqMmZjlQlziJFDHlxkDlNxsjCSnK0Fxk
    djAbq16F9t/C2HIVHK+zKhPGqVVfoeM9ksmngAIOUK05pz8665gjo61748WW+rqf
    f/WKiQtu28AjskPNY3CS2vnl3jbSqMrFsi1hPWV2dQRgzl5xI2qR+03sQHaaQUTc
    d/whE7t85PqmsVED2vmD9dGy6vTnd3nH8j8DEVaBO/y9bJbpyLZuivCuKxZX+GYg
    Mk3Ms7t3rsdPbpiiHK6lcfntwVxThBBSbSTqDvUb4GYryzdQ5B5ib4nEc/aI/msU
    ubFotuU6gIPvra9MI/NUSpA=
    -----END CERTIFICATE-----
    """.trimIndent()

    private val SERVER_KEY_PEM = """
    -----BEGIN PRIVATE KEY-----
    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
    paLEjX+8kznUisDokSzJ7iHoZgmN
    -----END PRIVATE KEY-----
    """.trimIndent()

}