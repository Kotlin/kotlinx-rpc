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
                field("foo2_bar")
                field("_leading_name")
                field("trailing_name_")
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
            interface Weird_Message_2 {
                val foo_Bar: String
                val foo_1Bar: String
                val foo2Bar: String
                val leadingName: String
                val trailingName_: String
                val already_HTTP_2Response: String
                val odd_Choice_2: Odd_Choice_2?

                sealed interface Odd_Choice_2 {
                    @JvmInline
                    value class First_Choice(val value: String): Odd_Choice_2

                    @JvmInline
                    value class Second_2Choice(val value: String): Odd_Choice_2
                }
            }
            """.trimIndent(),
        )
        assertContains(
            generated,
            """
            sealed class Status_Code_2(open val number: Int) {
                data object UNSPECIFIED: Status_Code_2(number = 0)

                data object ACTIVE: Status_Code_2(number = 1)
            """.trimIndent(),
        )
    }
}
