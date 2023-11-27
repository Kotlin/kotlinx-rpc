import util.optInForInternalKRPCApi

plugins {
    id("conventions-common")
    id("org.jetbrains.kotlin.jvm")
}

kotlin {
    jvmToolchain(8)

    optInForInternalKRPCApi()
}
