/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.grpc

import com.google.protobuf.DescriptorProtos
import kotlinx.rpc.protoc.gen.core.GeneratedMetadata
import kotlinx.rpc.protoc.gen.core.model.Model
import kotlinx.rpc.protoc.gen.grpc.fixture.proto.grpcGeneratorConfig
import kotlinx.rpc.protoc.gen.grpc.fixture.proto.grpcProto
import kotlinx.rpc.protoc.gen.grpc.fixture.proto.toGrpcModel
import kotlin.test.Test
import kotlin.test.assertContains

class ModelToGrpcKotlinCommonGeneratorTest {

    @Test
    fun `should generate idempotent annotation`() {
        val model = grpcProto {
            message("Request")
            message("Response")
            service("ExampleService") {
                rpc(
                    "IdempotentMethod",
                    input = "Request",
                    output = "Response",
                    idempotencyLevel = DescriptorProtos.MethodOptions.IdempotencyLevel.IDEMPOTENT,
                )
            }
        }.toGrpcModel()
        val file = generateGrpcFile(model = model, useLowerCamelCaseGrpcMethodNames = false)

        assertContains(
            file.trim(),
            """
            @Grpc
            interface ExampleService {
                @Grpc.Method(idempotent = true)
                suspend fun IdempotentMethod(message: Request): Response
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should generate idempotent and safe annotation`() {
        val model = grpcProto {
            message("Request")
            message("Response")
            service("ExampleService") {
                rpc(
                    "SafeMethod",
                    input = "Request",
                    output = "Response",
                    idempotencyLevel = DescriptorProtos.MethodOptions.IdempotencyLevel.NO_SIDE_EFFECTS,
                )
            }
        }.toGrpcModel()
        val file = generateGrpcFile(model = model, useLowerCamelCaseGrpcMethodNames = false)

        assertContains(
            file.trim(),
            """
            @Grpc
            interface ExampleService {
                @Grpc.Method(idempotent = true, safe = true)
                suspend fun SafeMethod(message: Request): Response
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should preserve method name when lowerCamelCase conversion is disabled`() {
        val model = grpcProto {
            message("Request")
            message("Response")
            service("ExampleService") {
                rpc("GetUser", input = "Request", output = "Response")
            }
        }.toGrpcModel()
        val file = generateGrpcFile(model = model, useLowerCamelCaseGrpcMethodNames = false)
        assertContains(
            file.trim(),
            """
            @Grpc
            interface ExampleService {
                suspend fun GetUser(message: Request): Response
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should convert UpperCamelCase method name to lowerCamelCase`() {
        val model = grpcProto {
            message("Request")
            message("Response")
            service("ExampleService") {
                rpc("GetUser", input = "Request", output = "Response")
            }
        }.toGrpcModel()
        val file = generateGrpcFile(model = model, useLowerCamelCaseGrpcMethodNames = true)

        assertContains(
            file.trim(),
            """
            @Grpc
            interface ExampleService {
                @Grpc.Method(name = "GetUser")
                suspend fun getUser(message: Request): Response
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should escape method name when converted to Kotlin keyword`() {
        val model = grpcProto {
            message("Request")
            message("Response")
            service("ExampleService") {
                rpc("Class", input = "Request", output = "Response")
            }
        }.toGrpcModel()
        val file = generateGrpcFile(model = model, useLowerCamelCaseGrpcMethodNames = true)

        assertContains(
            file.trim(),
            """
            @Grpc
            interface ExampleService {
                @Grpc.Method(name = "Class")
                suspend fun `class`(message: Request): Response
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should generate kdoc`() {
        val model = grpcProto {
            message("Request")
            message("Response")
            service("ExampleService") {
                rpc(
                    "DocumentedMethod",
                    input = "Request",
                    output = "Response",
                    comment = "Returns a documented response.",
                )
            }
        }.toGrpcModel()
        val file = generateGrpcFile(model = model, useLowerCamelCaseGrpcMethodNames = false)

        assertContains(
            file.trim(),
            """
            @Grpc
            interface ExampleService {
                /**
                 * Returns a documented response.
                 */
                suspend fun DocumentedMethod(message: Request): Response
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should return flow when the method is server streaming`() {
        val model = grpcProto {
            message("Request")
            message("Response")
            service("ExampleService") {
                rpc(
                    "StreamResponses",
                    input = "Request",
                    output = "Response",
                    serverStreaming = true,
                )
            }
        }.toGrpcModel()
        val file = generateGrpcFile(model = model, useLowerCamelCaseGrpcMethodNames = false)

        assertContains(
            file.trim(),
            """
            @Grpc
            interface ExampleService {
                fun StreamResponses(message: Request): Flow<Response>
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should accept flow when the method is client streaming`() {
        val model = grpcProto {
            message("Request")
            message("Response")
            service("ExampleService") {
                rpc(
                    "StreamRequests",
                    input = "Request",
                    output = "Response",
                    clientStreaming = true,
                )
            }
        }.toGrpcModel()
        val file = generateGrpcFile(model = model, useLowerCamelCaseGrpcMethodNames = false)

        assertContains(
            file.trim(),
            """
            @Grpc
            interface ExampleService {
                suspend fun StreamRequests(message: Flow<Request>): Response
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should generate deprecated annotation`() {
        val model = grpcProto {
            message("Request")
            message("Response")
            service("ExampleService") {
                rpc(
                    "DeprecatedMethod",
                    input = "Request",
                    output = "Response",
                    deprecated = true,
                )
            }
        }.toGrpcModel()
        val file = generateGrpcFile(model = model, useLowerCamelCaseGrpcMethodNames = false)

        assertContains(
            file.trim(),
            """
            @Grpc
            interface ExampleService {
                @Deprecated("This declaration is deprecated in .proto file")
                suspend fun DeprecatedMethod(message: Request): Response
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should convert snake_case method name to lowerCamelCase`() {
        val model = grpcProto {
            message("Request")
            message("Response")
            service("ExampleService") {
                rpc(
                    "snake_case_method_name",
                    input = "Request",
                    output = "Response",
                )
            }
        }.toGrpcModel()
        val file = generateGrpcFile(model = model, useLowerCamelCaseGrpcMethodNames = true)

        assertContains(
            file.trim(),
            """
            interface ExampleService {
                @Grpc.Method(name = "snake_case_method_name")
                suspend fun snakeCaseMethodName(message: Request): Response
            }
            """.trimIndent(),
        )
    }

    private fun generateGrpcFile(
        model: Model,
        useLowerCamelCaseGrpcMethodNames: Boolean,
    ): String {
        return ModelToGrpcKotlinCommonGenerator(
            config = grpcGeneratorConfig(useLowerCamelCaseGrpcMethodNames),
            generatedMetadata = GeneratedMetadata(),
            model = model,
        ).generateKotlinFiles().single().build()
    }
}
