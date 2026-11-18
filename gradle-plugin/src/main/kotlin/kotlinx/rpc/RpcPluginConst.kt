/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.rpc.RpcPluginConst.PROTOC_ENABLED_PROPERTY
import org.gradle.api.Project

internal object RpcPluginConst {
    const val PROTOC_ENABLED_PROPERTY = "kotlinx.rpc.protoc"
}

internal val Project.isProtocEnabled: Boolean
    get() {
        return providers.gradleProperty(PROTOC_ENABLED_PROPERTY)
            .map(String::toBoolean)
            .getOrElse(false)
    }
