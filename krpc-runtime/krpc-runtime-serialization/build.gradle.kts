plugins {
    alias(libs.plugins.conventions.kmp)
}

kmp {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.serialization.core)
            }
        }
    }
}
