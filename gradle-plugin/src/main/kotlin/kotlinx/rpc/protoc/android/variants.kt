/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.android

import com.android.build.api.variant.Variant

// root source sets for 'com.android.(application|library|test|dynamic-feature)'
internal enum class LegacyAndroidRootSourceSets(val stringValue: String) {
    Main("main"),
    Test("test"),
    AndroidTest("androidTest"),
    TestFixtures("testFixtures"),
    ;

    override fun toString(): String = stringValue
}

// leaf (not root as above) source sets for 'com.android.kotlin.multiplatform.library'
internal enum class KmpLibraryAndroidLeafSourceSets(val sourceSetName: String) {
    Main("androidMain"),
    HostTest("androidHostTest"),
    DeviceTest("androidDeviceTest"),
    ;

    override fun toString(): String = sourceSetName
}

// returns a list of source set names this variant consists
// for armFreeappDebugTest (where arm is a flavor):
// - test
// - testArm
// - testFreeapp
// - testArmFreeapp
// - testDebug
// - testArmFreeappDebug
internal fun Variant.dependencySourceSets(rootName: LegacyAndroidRootSourceSets) = buildList {
    fun String.prefixed() = prefixed(rootName)

    add(rootName.stringValue)

    productFlavors.reversed().forEach { (_, flavorName) ->
        add(flavorName.prefixed())
    }

    if (productFlavors.size > 1) {
        val combinationName = productFlavors
            .joinToString("") { (_, name) -> name.replaceFirstChar { it.uppercase() } }
            .replaceFirstChar { it.lowercase() }

        add(combinationName.prefixed())
    }

    buildType?.let { add(it.prefixed()) }
    add(name.prefixed())
}

// when com.android.* (not kotlin.multiplatform.library) is applied - kotlin has proxy source sets:
// | AndroidSourceSet | KotlinSourceSet |
// | main             | androidMain     |
// | test             | androidUnitTest |
// | debug            | androidDebug    |
// ...
//
// these kotlin 'proxy' source sets have propper dependsOn values, and should have tasks configured for them
// instead of tasks being configured for the android source sets
//
// Examples:
// debug -> androidDebug
// androidTest -> androidInstrumentedTest
// androidTestDebug -> androidInstrumentedTestDebug
// main -> androidMain
// release -> androidRelease
// test -> androidUnitTest
// testDebug -> androidUnitTestDebug
// testRelease -> androidUnitTestRelease
internal fun String.kotlinProxyFromAndroidOriginSourceSetName(rootName: LegacyAndroidRootSourceSets): String? {
    return when (rootName) {
        LegacyAndroidRootSourceSets.Main -> {
            "android${replaceFirstChar { it.uppercase() }}"
        }

        LegacyAndroidRootSourceSets.Test -> {
            "androidUnit${replaceFirstChar { it.uppercase() }}"
        }

        LegacyAndroidRootSourceSets.AndroidTest -> {
            "androidInstrumented${removePrefix("android").replaceFirstChar { it.uppercase() }}"
        }

        LegacyAndroidRootSourceSets.TestFixtures -> null
    }
}

internal fun String.prefixed(rootName: LegacyAndroidRootSourceSets) =
    if (rootName == LegacyAndroidRootSourceSets.Main) {
        this
    } else {
        "${rootName.stringValue}${this.replaceFirstChar { it.uppercase() }}"
    }
