import util.kotlin
import util.optInForInternalKRPCApi

plugins {
    id("conventions-common")
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    optInForInternalKRPCApi()
}
