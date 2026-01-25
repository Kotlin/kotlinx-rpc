/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.other

import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.NamedDomainObjectProvider

internal fun <T: Any> NamedDomainObjectCollection<T>.maybeNamed(name: String): NamedDomainObjectProvider<T>? {
    return if (name in names) named(name) else null
}

internal fun <T: Any> NamedDomainObjectCollection<T>.maybeNamed(name: String, configure: T.() -> Unit) {
    if (name in names) named(name).configure(configure)
}
