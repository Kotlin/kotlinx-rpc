/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.other

import org.gradle.api.Project
import org.gradle.internal.extensions.core.extra
import org.gradle.kotlin.dsl.provideDelegate
import java.util.Properties
import java.util.concurrent.atomic.AtomicReference
import kotlin.io.path.Path
import kotlin.io.path.inputStream

private val ref = AtomicReference<Properties>()

fun Project.localProperties(): Properties {
    if (ref.get() == null) {
        ref.compareAndSet(null, Properties().apply {
            val globalRootDir: String by extra

            load(Path(globalRootDir, "local.properties").inputStream())
        })
    }

    return ref.get()
}
