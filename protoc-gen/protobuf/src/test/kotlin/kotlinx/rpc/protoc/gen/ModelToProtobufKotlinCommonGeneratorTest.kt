/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen

import kotlinx.rpc.protoc.gen.core.Config
import kotlinx.rpc.protoc.gen.core.GeneratedMetadata
import kotlinx.rpc.protoc.gen.core.Platform
import kotlinx.rpc.protoc.gen.fixture.proto.protobufProto
import kotlinx.rpc.protoc.gen.fixture.proto.toGeneratorModel
import kotlin.test.Test
import kotlin.test.assertContains

class ModelToProtobufKotlinCommonGeneratorTest {
    @Test
    fun `camel case option converts protobuf declaration names`() {
        val config = Config(
            explicitApiModeEnabled = false,
            generateComments = false,
            generateFileLevelComments = false,
            generateOptionalFieldOrNullGetters = false,
            indentSize = 4,
            platform = Platform.Jvm,
            protoNamesOutput = null,
            camelCaseNames = true,
        )
        val model = protobufProto {
            message("user_profile") {
                field("display_name")
                oneOf("contact_info") {
                    field("email_address")
                    field("phone_number")
                }
            }
            enumType(
                "account_state",
                "ACCOUNT_STATE_UNSPECIFIED",
                "ACCOUNT_STATE_ACTIVE",
            )
        }.toGeneratorModel(config)

        val generated = ModelToProtobufKotlinCommonGenerator(
            config = config,
            generatedMetadata = GeneratedMetadata(),
            model = model,
        ).generateKotlinFiles().first().build()

        assertContains(
            generated,
            """
            @GeneratedProtoMessage
            interface UserProfile {
                val displayName: String
                val contactInfo: ContactInfo?

                sealed interface ContactInfo {
                    @JvmInline
                    value class EmailAddress(val value: String): ContactInfo

                    @JvmInline
                    value class PhoneNumber(val value: String): ContactInfo
                }
            }
            """.trimIndent(),
        )
        assertContains(
            generated,
            """
            sealed class AccountState(open val number: Int) {
                data object UNSPECIFIED: AccountState(number = 0)

                data object ACTIVE: AccountState(number = 1)
            """.trimIndent(),
        )
    }

    @Test
    fun `disabling camel case option preserves protobuf declaration names`() {
        val config = Config(
            explicitApiModeEnabled = false,
            generateComments = false,
            generateFileLevelComments = false,
            generateOptionalFieldOrNullGetters = false,
            indentSize = 4,
            platform = Platform.Jvm,
            protoNamesOutput = null,
            camelCaseNames = false,
        )
        val model = protobufProto {
            message("user_profile") {
                field("display_name")
                field("_leading_name")
                field("trailing_name_")
                field("foo__bar")
                oneOf("contact_info") {
                    field("email_address")
                    field("phone_number")
                }
            }
            enumType(
                "account_state",
                "ACCOUNT_STATE_UNSPECIFIED",
                "ACCOUNT_STATE_ACTIVE",
            )
        }.toGeneratorModel(config)

        val generated = ModelToProtobufKotlinCommonGenerator(
            config = config,
            generatedMetadata = GeneratedMetadata(),
            model = model,
        ).generateKotlinFiles().first().build()

        assertContains(
            generated,
            """
            @GeneratedProtoMessage
            interface user_profile {
                val display_name: String
                val _leading_name: String
                val trailing_name_: String
                val foo__bar: String
                val contact_info: contact_info?

                sealed interface contact_info {
                    @JvmInline
                    value class email_address(val value: String): contact_info

                    @JvmInline
                    value class phone_number(val value: String): contact_info
                }
            }
            """.trimIndent(),
        )
        assertContains(
            generated,
            """
            sealed class account_state(open val number: Int) {
                data object UNSPECIFIED: account_state(number = 0)

                data object ACTIVE: account_state(number = 1)
            """.trimIndent(),
        )
    }

    @Test
    fun `camel case option handles underscores and digits in protobuf declaration names`() {
        val config = Config(
            explicitApiModeEnabled = false,
            generateComments = false,
            generateFileLevelComments = false,
            generateOptionalFieldOrNullGetters = false,
            indentSize = 4,
            platform = Platform.Jvm,
            protoNamesOutput = null,
            camelCaseNames = true,
        )
        val model = protobufProto {
            message("weird__message_2") {
                field("foo__bar")
                field("foo_1_bar")
                field("foo_2fa")
                field("foo2_bar")
                field("HTTP_status")
                field("_leading_name")
                field("__double_leading_name")
                field("_GetUser")
                field("__GetUser__")
                field("trailing_name_")
                field("double_trailing_name__")
                field("already_HTTP_2_response")
                oneOf("odd__choice_2") {
                    field("first__choice")
                    field("second_2_choice")
                }
            }
            enumType(
                "status__code_2",
                "STATUS__CODE_2_UNSPECIFIED",
                "STATUS__CODE_2_ACTIVE",
            )
        }.toGeneratorModel(config)

        val generated = ModelToProtobufKotlinCommonGenerator(
            config = config,
            generatedMetadata = GeneratedMetadata(),
            model = model,
        ).generateKotlinFiles().first().build()

        assertContains(
            generated,
            """
            @GeneratedProtoMessage
            interface WeirdMessage2 {
                val fooBar: String
                val foo1Bar: String
                val foo2fa: String
                val foo2Bar: String
                val httpStatus: String
                val _leadingName: String
                val __doubleLeadingName: String
                val _getUser: String
                val __getUser__: String
                val trailingName_: String
                val doubleTrailingName__: String
                val alreadyHTTP2Response: String
                val oddChoice2: OddChoice2?

                sealed interface OddChoice2 {
                    @JvmInline
                    value class FirstChoice(val value: String): OddChoice2

                    @JvmInline
                    value class Second2Choice(val value: String): OddChoice2
                }
            }
            """.trimIndent(),
        )
        assertContains(
            generated,
            """
            sealed class StatusCode2(open val number: Int) {
                data object UNSPECIFIED: StatusCode2(number = 0)

                data object ACTIVE: StatusCode2(number = 1)
            """.trimIndent(),
        )
    }
}
