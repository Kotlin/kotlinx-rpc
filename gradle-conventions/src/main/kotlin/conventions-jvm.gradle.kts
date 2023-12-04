import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import util.optInForInternalKRPCApi

plugins {
    id("conventions-common")
    id("org.jetbrains.kotlin.jvm")
}

configure<KotlinJvmProjectExtension> {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }

    optInForInternalKRPCApi()
}
