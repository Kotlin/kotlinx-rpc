/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// NOT COPIED from:
// https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/DescriptorsTest.java
//
// Reason: All ~60 tests in DescriptorsTest.java exercise the Java Descriptor/Reflection API
// (FileDescriptor, Descriptor, FieldDescriptor, EnumDescriptor, ServiceDescriptor, DynamicMessage,
// OneofDescriptor, DescriptorValidationException, etc.) which has no equivalent in kotlinx-rpc's
// protobuf implementation. The kotlinx-rpc protobuf generator produces strongly-typed Kotlin
// interfaces without a runtime descriptor/reflection layer comparable to Java's protobuf library.
//
// The only descriptor-like API in kotlinx-rpc is ProtoDescriptor<T> (providing fullName),
// which is tested separately in protobuf-api module tests.

package proto2_unittest
