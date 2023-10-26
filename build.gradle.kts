plugins {
    kotlin("plugin.serialization") version "1.9.10" apply false
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
    id("kotlinx-atomicfu") version "0.22.0" apply false
}

allprojects {
    group = "org.jetbrains.krpc"
    version = "1.9.10-beta-4.2"

    repositories {
        mavenCentral()
    }
}
