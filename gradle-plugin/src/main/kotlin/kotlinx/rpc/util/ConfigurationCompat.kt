/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.util

import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.util.GradleVersion

private val SUPPORTS_LAZY_EXTENDS_FROM: Boolean =
    GradleVersion.current().baseVersion >= GradleVersion.version("9.4")

@Suppress("UnstableApiUsage")
internal fun Configuration.extendsFromLazy(
    legacyList: ListProperty<Configuration>,
    provider: Provider<out Configuration>,
) {
    if (SUPPORTS_LAZY_EXTENDS_FROM) {
        extendsFrom(provider)
    } else {
        legacyList.add(provider)
    }
}
