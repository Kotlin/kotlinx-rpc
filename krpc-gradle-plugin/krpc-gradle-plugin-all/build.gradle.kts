description = "kRPC Gradle Plugin"

dependencies {
    implementation(project(":krpc-gradle-plugin-api"))
    implementation(project(":krpc-gradle-plugin-platform"))

    compileOnly(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        create("krpc-all") {
            id = "org.jetbrains.krpc.plugin"

            displayName = "kRPC Gradle Plugin"
            implementationClass = "org.jetbrains.krpc.KRPCGradlePlugin"
            description = """
                The plugin ensures correct configurations for your project, that will allow proper kRPC code generation. 
                Additionally, it enforces proper artifacts versions for your project, depending on your Kotlin version. (via "org.jetbrains.krpc.platform" plugin)
            """.trimIndent()
        }
    }
}
