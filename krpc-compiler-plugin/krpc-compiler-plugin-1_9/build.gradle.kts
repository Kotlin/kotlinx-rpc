plugins {
    alias(libs.plugins.conventions.jvm)
}

dependencies {
    compileOnly(libs.kotlin.compiler.embeddable)
}
