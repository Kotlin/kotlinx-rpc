import util.other.isPublicModule
import util.other.libs

/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

if (isPublicModule) {
    plugins.apply(libs.plugins.conventions.dokka.spec.get().pluginId)
}
