/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.otherwise
import util.whenKotlinLatest

/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

rootProject.name = "gradle-conventions"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("../gradle-conventions-settings")
}

includeBuild("../gradle-conventions-settings")

plugins {
    id("conventions-repositories")
    id("conventions-version-resolution")
}

whenKotlinLatest {
    include(":latest-only")
} otherwise {
    include(":empty")
}
