package util

import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

internal fun KotlinProjectExtension.optInForInternalKRPCApi() {
    sourceSets.all {
        languageSettings.optIn("org.jetbrains.krpc.internal.InternalKRPCApi")
    }
}
