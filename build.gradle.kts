plugins {
    kotlin("plugin.serialization") version "1.9.10" apply false
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
}

allprojects {
    group = "org.jetbrains.krpc"
    version = "1.9.10-beta-1"

    repositories {
        mavenCentral()
    }
}
