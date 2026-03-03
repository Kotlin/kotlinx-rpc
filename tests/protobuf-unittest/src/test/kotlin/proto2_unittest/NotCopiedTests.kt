/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

/**
 * This file documents Java protobuf tests that were NOT copied to this module or to protobuf-unittest.
 * Each entry includes a link to the original test and the reason it was not translated.
 *
 * Source: https://github.com/protocolbuffers/protobuf/tree/main/java/core/src/test/java/com/google/protobuf
 *
 * ===========================================================================================================
 * Tests already translated in :tests:protobuf-unittest module:
 * ===========================================================================================================
 *
 * - GeneratedMessageTest.java → GeneratedMessageTest.kt
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/GeneratedMessageTest.java
 *
 * - MessageTest.java → MessageTest.kt
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/MessageTest.java
 *
 * - WireFormatTest.java → WireFormatTest.kt
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/WireFormatTest.java
 *
 * - ParserTest.java → ParserTest.kt
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/ParserTest.java
 *
 * - TestUtil.java → TestUtil.kt
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/TestUtil.java
 *
 * ===========================================================================================================
 * Tests translated in this module (:tests:protobuf-unittest):
 * ===========================================================================================================
 *
 * - TestBadIdentifiers.java → TestBadIdentifiers.kt (partial — uses BadFieldNames from unittest.proto)
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/TestBadIdentifiers.java
 *
 * - MapTest.java → MapFieldTest.kt (applicable map operation tests)
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/MapTest.java
 *
 * - MapForProto2Test.java → MapFieldTest.kt (applicable map operation tests)
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/MapForProto2Test.java
 *
 * - evil_names_proto2.proto / evil_names_proto3.proto → EvilNamesProto2Test.kt, EvilNamesProto3Test.kt
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/proto/com/google/protobuf/evil_names_proto2.proto
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/proto/com/google/protobuf/evil_names_proto3.proto
 *
 * ===========================================================================================================
 * Tests NOT copied — Java internal runtime APIs (no equivalent in kotlinx-rpc):
 * ===========================================================================================================
 *
 * - AbstractMessageTest.java: Tests Java AbstractMessage base class API (toBuilder, mergeFrom, reflection)
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/AbstractMessageTest.java
 *
 * - AbstractProto2LiteSchemaTest.java: Tests Java internal Schema/Protobuf runtime API
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/AbstractProto2LiteSchemaTest.java
 *
 * - AbstractProto2SchemaTest.java: Tests Java internal Schema runtime API
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/AbstractProto2SchemaTest.java
 *
 * - AbstractProto3LiteSchemaTest.java: Tests Java internal Schema runtime API
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/AbstractProto3LiteSchemaTest.java
 *
 * - AbstractProto3SchemaTest.java: Tests Java internal Schema runtime API
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/AbstractProto3SchemaTest.java
 *
 * - AbstractSchemaTest.java: Tests Java internal Schema runtime API
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/AbstractSchemaTest.java
 *
 * - ArrayDecodersTest.java: Tests Java internal ArrayDecoders class
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/ArrayDecodersTest.java
 *
 * - BinaryProtocolTest.java: Tests Java internal BinaryProtocol class
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/BinaryProtocolTest.java
 *
 * - BooleanArrayListTest.java: Tests Java internal BooleanArrayList collection
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/BooleanArrayListTest.java
 *
 * - BoundedByteStringTest.java: Tests Java ByteString internals (bounded substring)
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/BoundedByteStringTest.java
 *
 * - ByteBufferWriterTest.java: Tests Java ByteBuffer writing API
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/ByteBufferWriterTest.java
 *
 * - ByteStringTest.java: Tests Java ByteString API (our API uses Kotlin ByteArray)
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/ByteStringTest.java
 *
 * - CachedFieldSizeTest.java: Tests Java cached serialized size internals
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/CachedFieldSizeTest.java
 *
 * - CodedAdapterTest.java: Tests Java CodedInputStream/OutputStream adapters
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/CodedAdapterTest.java
 *
 * - CodedInputStreamTest.java: Tests Java CodedInputStream API
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/CodedInputStreamTest.java
 *
 * - CodedOutputStreamTest.java: Tests Java CodedOutputStream API
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/CodedOutputStreamTest.java
 *
 * - ConcurrentDescriptorsTest.java: Tests concurrent access to Java Descriptors
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/ConcurrentDescriptorsTest.java
 *
 * - DoubleArrayListTest.java: Tests Java internal DoubleArrayList collection
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/DoubleArrayListTest.java
 *
 * - FloatArrayListTest.java: Tests Java internal FloatArrayList collection
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/FloatArrayListTest.java
 *
 * - IntArrayListTest.java: Tests Java internal IntArrayList collection
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/IntArrayListTest.java
 *
 * - LongArrayListTest.java: Tests Java internal LongArrayList collection
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/LongArrayListTest.java
 *
 * - ProtobufArrayListTest.java: Tests Java internal ProtobufArrayList collection
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/ProtobufArrayListTest.java
 *
 * - LazyStringArrayListTest.java: Tests Java internal LazyStringArrayList
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/LazyStringArrayListTest.java
 *
 * - UnmodifiableLazyStringListTest.java: Tests Java internal UnmodifiableLazyStringList
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/UnmodifiableLazyStringListTest.java
 *
 * - SmallSortedMapTest.java: Tests Java internal SmallSortedMap collection
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/SmallSortedMapTest.java
 *
 * - SingleFieldBuilderTest.java: Tests Java SingleFieldBuilder (nested builder pattern)
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/SingleFieldBuilderTest.java
 *
 * - RepeatedFieldBuilderTest.java: Tests Java RepeatedFieldBuilder (nested builder pattern)
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/RepeatedFieldBuilderTest.java
 *
 * - LiteralByteStringTest.java: Tests Java ByteString internals (literal implementation)
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/LiteralByteStringTest.java
 *
 * - RopeByteStringTest.java: Tests Java ByteString internals (rope implementation)
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/RopeByteStringTest.java
 *
 * - RopeByteStringSubstringTest.java: Tests Java ByteString internals (rope substring)
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/RopeByteStringSubstringTest.java
 *
 * - IterableByteBufferInputStreamTest.java: Tests Java ByteBuffer InputStream adapter
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/IterableByteBufferInputStreamTest.java
 *
 * ===========================================================================================================
 * Tests NOT copied — Java Descriptor/Reflection API:
 * ===========================================================================================================
 *
 * - DescriptorsTest.java: Tests Java Descriptors API (field descriptors, message descriptors, etc.)
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/DescriptorsTest.java
 *   Also documented in :tests:protobuf-unittest:DescriptorsTest.kt
 *
 * - EnumTest.java: Tests enum descriptors via Java Descriptors API
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/EnumTest.java
 *
 * - FieldPresenceTest.java: Tests field presence via Java Descriptors and hasMethod reflection
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/FieldPresenceTest.java
 *
 * - ServiceTest.java: Tests Java Service/RPC descriptors
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/ServiceTest.java
 *
 * - ImportOptionTest.java: Tests Java import option via Descriptors
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/ImportOptionTest.java
 *
 * ===========================================================================================================
 * Tests NOT copied — Java DynamicMessage API:
 * ===========================================================================================================
 *
 * - DynamicMessageTest.java: Tests Java DynamicMessage (runtime message construction from descriptors)
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/DynamicMessageTest.java
 *
 * ===========================================================================================================
 * Tests NOT copied — Java Extensions API:
 * ===========================================================================================================
 *
 * - ExtensionRegistryFactoryTest.java: Tests Java ExtensionRegistry factory
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/ExtensionRegistryFactoryTest.java
 *
 * - Proto2ExtensionLookupSchemaTest.java: Tests Java Extension lookup in Schema API
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/Proto2ExtensionLookupSchemaTest.java
 *
 * ===========================================================================================================
 * Tests NOT copied — Java TextFormat API:
 * ===========================================================================================================
 *
 * - TextFormatTest.java: Tests Java TextFormat print/parse (~2300 lines)
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/TextFormatTest.java
 *
 * - TextFormatParseInfoTreeTest.java: Tests Java TextFormat parse info tree
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/TextFormatParseInfoTreeTest.java
 *
 * - TextFormatParseLocationTest.java: Tests Java TextFormat parse location
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/TextFormatParseLocationTest.java
 *
 * - TextFormatPerformanceTest.java: Tests Java TextFormat performance
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/TextFormatPerformanceTest.java
 *
 * - LegacyUnredactedTextFormatTest.java: Tests Java legacy unredacted text format
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/LegacyUnredactedTextFormatTest.java
 *
 * ===========================================================================================================
 * Tests NOT copied — Java debug/redaction format:
 * ===========================================================================================================
 *
 * - DebugFormatTest.java: Tests Java debug format for proto messages
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/DebugFormatTest.java
 *
 * - ProtobufToStringOutputTest.java: Tests Java toString/debug redaction format
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/ProtobufToStringOutputTest.java
 *
 * - UnredactedDebugFormatForTestTest.java: Tests Java unredacted debug format
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/UnredactedDebugFormatForTestTest.java
 *
 * ===========================================================================================================
 * Tests NOT copied — Java UnknownFieldSet API:
 * ===========================================================================================================
 *
 * - UnknownFieldSetTest.java: Tests Java UnknownFieldSet (field building, cloning, serialization)
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/UnknownFieldSetTest.java
 *
 * - UnknownFieldSetPerformanceTest.java: Tests Java UnknownFieldSet performance
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/UnknownFieldSetPerformanceTest.java
 *
 * - DiscardUnknownFieldsTest.java: Tests Java DiscardUnknownFieldsParser wrapper
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/DiscardUnknownFieldsTest.java
 *
 * ===========================================================================================================
 * Tests NOT copied — Java unknown enum value handling:
 * ===========================================================================================================
 *
 * - UnknownEnumValueTest.java: Tests unknown enum values via Java Descriptors + DynamicMessage
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/UnknownEnumValueTest.java
 *
 * - Proto2UnknownEnumValueTest.java: Tests proto2 unknown enum values via Java Descriptors
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/Proto2UnknownEnumValueTest.java
 *
 * ===========================================================================================================
 * Tests NOT copied — Java UTF-8 handling internals:
 * ===========================================================================================================
 *
 * - CheckUtf8Test.java: Tests Java java_string_check_utf8 file option
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/CheckUtf8Test.java
 *
 * - Utf8Test.java: Tests Java Utf8 internal utility class
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/Utf8Test.java
 *
 * - DecodeUtf8Test.java: Tests Java UTF-8 decoding internals
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/DecodeUtf8Test.java
 *
 * - IsValidUtf8Test.java: Tests Java UTF-8 validation internals
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/IsValidUtf8Test.java
 *
 * ===========================================================================================================
 * Tests NOT copied — Java lazy field internals:
 * ===========================================================================================================
 *
 * - LazyFieldTest.java: Tests Java lazy field deserialization internals
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/LazyFieldTest.java
 *
 * - LazyFieldLiteTest.java: Tests Java lite lazy field internals
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/LazyFieldLiteTest.java
 *
 * - LazyMessageLiteTest.java: Tests Java lazy message lite internals
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/LazyMessageLiteTest.java
 *
 * - LazilyParsedMessageSetTest.java: Tests Java lazily parsed message sets
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/LazilyParsedMessageSetTest.java
 *
 * - LazyStringEndToEndTest.java: Tests Java lazy string conversion (illegal UTF-8 round-trips)
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/LazyStringEndToEndTest.java
 *
 * ===========================================================================================================
 * Tests NOT copied — Java nested builder pattern:
 * ===========================================================================================================
 *
 * - NestedBuildersTest.java: Tests Java getEngineBuilder()/addWheelBuilder() nested builder pattern
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/NestedBuildersTest.java
 *
 * ===========================================================================================================
 * Tests NOT copied — Java-specific code generation:
 * ===========================================================================================================
 *
 * - DeprecatedFieldTest.java: Tests Java @Deprecated annotation on generated fields
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/DeprecatedFieldTest.java
 *
 * - GeneratorNamesTest.java: Tests Java GeneratorNames utility (file class names, bytecode names)
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/GeneratorNamesTest.java
 *
 * - RuntimeVersionTest.java: Tests Java runtime version checking
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/RuntimeVersionTest.java
 *
 * ===========================================================================================================
 * Tests NOT copied — Java parse exception handling:
 * ===========================================================================================================
 *
 * - ParseExceptionsTest.java: Tests Java InvalidProtocolBufferException details
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/ParseExceptionsTest.java
 *
 * - InvalidProtocolBufferExceptionTest.java: Tests Java exception API
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/InvalidProtocolBufferExceptionTest.java
 *
 * ===========================================================================================================
 * Tests NOT copied — Java lite runtime:
 * ===========================================================================================================
 *
 * - TestBadIdentifiersLite.java: Lite version of TestBadIdentifiers (same proto dependency)
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/TestBadIdentifiersLite.java
 *
 * - LiteEqualsAndHashTest.java: Tests lite message equals/hashCode
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/LiteEqualsAndHashTest.java
 *
 * - LargeEnumLiteTest.java: Tests large enum in lite runtime
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/LargeEnumLiteTest.java
 *
 * - ParserLiteTest.java: Tests lite parser API
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/ParserLiteTest.java
 *
 * - MapLiteTest.java: Tests lite map API
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/MapLiteTest.java
 *
 * - WireFormatLiteTest.java: Tests Java WireFormatLite internal API
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/WireFormatLiteTest.java
 *
 * - WrappersLiteOfMethodTest.java: Tests Java lite Wrappers of() method
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/WrappersLiteOfMethodTest.java
 *
 * ===========================================================================================================
 * Tests NOT copied — Java Schema API (internal runtime):
 * ===========================================================================================================
 *
 * - Proto2SchemaTest.java, Proto2LiteSchemaTest.java, Proto3SchemaTest.java, Proto3LiteSchemaTest.java:
 *   All test Java internal Schema runtime API
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/Proto2SchemaTest.java
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/Proto2LiteSchemaTest.java
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/Proto3SchemaTest.java
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/Proto3LiteSchemaTest.java
 *
 * ===========================================================================================================
 * Tests NOT copied — Java Well-Known Types / Any / TypeRegistry:
 * ===========================================================================================================
 *
 * - WellKnownTypesTest.java: Tests Java WKT + Extensions API
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/WellKnownTypesTest.java
 *
 * - AnyTest.java: Tests Java Any type packing/unpacking
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/AnyTest.java
 *
 * - TypeRegistryTest.java: Tests Java TypeRegistry API
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/TypeRegistryTest.java
 *
 * - WrappersOfMethodTest.java: Tests Java Wrappers of() method
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/WrappersOfMethodTest.java
 *
 * ===========================================================================================================
 * Tests NOT copied — Java packed field specifics:
 * ===========================================================================================================
 *
 * - PackedFieldTest.java: Tests Java packed field via Builder getXOrDefault/extension patterns
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/PackedFieldTest.java
 *
 * - LargeEnumTest.java: Tests large enum values with Java Descriptors API
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/LargeEnumTest.java
 *
 * ===========================================================================================================
 * Test utilities NOT copied (not tests themselves):
 * ===========================================================================================================
 *
 * - TestUtilLite.java: Java lite test utility
 * - Proto2MessageFactory.java, Proto2MessageLiteFactory.java: Java test factories
 * - Proto3MessageFactory.java, Proto3MessageLiteFactory.java: Java test factories
 * - ExperimentalMessageFactory.java, ExperimentalSerializationUtil.java, ExperimentalTestDataProvider.java: Java experimental test utilities
 * - TestSchemas.java, TestSchemasLite.java: Java test schema definitions
 * - IsValidUtf8TestUtil.java: Java UTF-8 test utility
 *
 * ===========================================================================================================
 * Kotlin proto test files:
 * ===========================================================================================================
 *
 * - example_extensible_message.proto: Requires extensions support
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/proto/com/google/protobuf/example_extensible_message.proto
 *
 * - multiple_files_proto3.proto: Tests Java java_multiple_files option (not relevant for Kotlin generator)
 *   https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/proto/com/google/protobuf/multiple_files_proto3.proto
 */

package proto2_unittest
