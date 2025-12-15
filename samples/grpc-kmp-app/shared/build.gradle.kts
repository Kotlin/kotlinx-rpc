
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxRpc)
}

kotlin {
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

