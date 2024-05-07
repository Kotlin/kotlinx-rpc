/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.GroupingEntityType
import kotlinx.kover.gradle.plugin.dsl.KoverReportExtension
import kotlinx.kover.gradle.plugin.dsl.MetricType

apply(plugin = "org.jetbrains.kotlinx.kover")

the<KoverReportExtension>().apply {
    defaults {
        html {
            onCheck = false
            charset = "UTF_8"
            setReportDir(rootDir.resolve("kover"))
        }

        verify {
            onCheck = false

            rule {
                entity = GroupingEntityType.APPLICATION
                isEnabled = true

                bound {
                    metric = MetricType.LINE
                    aggregation = AggregationType.COVERED_PERCENTAGE
                    minValue = 70
                }
            }
        }
    }
}
