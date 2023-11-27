plugins {
    alias(libs.plugins.gradle.kotlin.dsl)
}

configurations.configureEach {
    resolutionStrategy {
        force(libs.kotlin.reflect)
        force(libs.kotlin.stdlib)
        force(libs.kotlin.stdlib.jdk7)
        force(libs.kotlin.stdlib.jdk8)
    }
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        named("conventions-common") {
            version = libs.versions.krpc.core.get()
        }
    }

    plugins {
        named("conventions-jvm") {
            version = libs.versions.krpc.core.get()
        }
    }

    plugins {
        named("conventions-kmp") {
            version = libs.versions.krpc.core.get()
        }
    }
}
