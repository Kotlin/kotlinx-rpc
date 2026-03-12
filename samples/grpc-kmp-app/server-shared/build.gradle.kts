plugins {
    alias(libs.plugins.kotlinJvm)
}

group = "kotlinx.rpc.sample"
version = "1.0.0"

dependencies {
    implementation(projects.shared)
    implementation(libs.kotlinx.rpc.grpc.server)
}
