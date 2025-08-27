/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import kotlinx.kover.gradle.plugin.dsl.GroupingEntityType

plugins {
//    id("org.jetbrains.kotlinx.kover")
}

//kover {
//    reports {
//        total {
//            html {
//                onCheck.set(false)
//                charset.set("UTF-8")
//                htmlDir.set(rootDir.resolve("kover"))
//            }
//
//            verify {
//                onCheck.set(false)
//
//                rule {
//                    groupBy.set(GroupingEntityType.APPLICATION)
//
//                    bound {
//                        coverageUnits.set(CoverageUnit.LINE)
//                        aggregationForGroup.set(AggregationType.COVERED_PERCENTAGE)
//                        minValue.set(70)
//                    }
//                }
//            }
//        }
//    }
//}
