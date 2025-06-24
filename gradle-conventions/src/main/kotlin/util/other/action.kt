/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.other

enum class ActionApplied {
    Applied, NotApplied;
}

@Suppress("unused")
infix fun ActionApplied.otherwise(body: () -> Unit) {
    if (this == ActionApplied.NotApplied) {
        body()
    }
}
