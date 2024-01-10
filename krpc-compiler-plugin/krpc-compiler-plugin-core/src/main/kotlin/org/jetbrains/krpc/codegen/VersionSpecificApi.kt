/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.codegen

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.krpc.internal.service.CompanionServiceContainer

interface VersionSpecificApi {
    fun isJs(platform: TargetPlatform?): Boolean

    fun referenceClass(context: IrPluginContext, packageName: String, name: String): IrClassSymbol?

    companion object : CompanionServiceContainer<VersionSpecificApi>(VersionSpecificApi::class)
}
