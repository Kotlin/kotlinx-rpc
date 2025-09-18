plugins {
    alias(libs.plugins.kotlinJvm)
    application
}

group = "kotlinx.rpc.sample"
version = "1.0.0"
application {
    mainClass.set("kotlinx.rpc.sample.ApplicationKt")
    
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.kotlinx.rpc.protobuf.core)
    implementation(libs.kotlinx.rpc.grpc.core)
    implementation(libs.grpc.netty)
}