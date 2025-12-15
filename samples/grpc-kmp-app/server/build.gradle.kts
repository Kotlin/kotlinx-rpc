plugins {
    alias(libs.plugins.kotlinJvm)
    application
}

group = "kotlinx.rpc.sample"
version = "1.0.0"
application {
    mainClass.set("kotlinx.rpc.sample.ApplicationKt")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.kotlinx.rpc.grpc.server)
    implementation(libs.grpc.netty)
}