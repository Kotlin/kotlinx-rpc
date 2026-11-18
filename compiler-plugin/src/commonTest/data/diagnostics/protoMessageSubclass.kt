/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// RUN_PIPELINE_TILL: FRONTEND

// MODULE: main
// FILE: module_main_protoMessageSubclass.kt

package test.user_code

import test.compiler.plugin.SimpleMessage

// SubclassOptInRequired(ProtobufMessagesInheritance::class) added by the compiler plugin
// prevents inheriting from generated proto messages
class UserImplementation : <!OPT_IN_TO_INHERITANCE_ERROR!>SimpleMessage<!> {
    override val name: String = ""
    override val value: Int = 0
}

/* GENERATED_FIR_TAGS: classDeclaration, integerLiteral, override, propertyDeclaration, stringLiteral */
