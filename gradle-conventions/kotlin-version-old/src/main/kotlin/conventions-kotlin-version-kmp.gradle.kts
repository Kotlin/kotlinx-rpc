/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.optionalProperty
import util.setLanguageVersion

val useK2Plugin: Boolean by optionalProperty()

setLanguageVersion(useK2Plugin)
