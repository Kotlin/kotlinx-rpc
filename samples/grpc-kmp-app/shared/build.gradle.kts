plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.kotlinxRpc)
}

kotlin {
    android {
        namespace = "kotlinx.rpc.sample.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    iosArm64()
    iosSimulatorArm64()

    jvm()

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.rpc.grpc.core)
            api(libs.kotlinx.rpc.protobuf.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

rpc {
    protoc()
}
