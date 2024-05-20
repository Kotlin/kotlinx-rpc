/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

apply(plugin = "com.gradle.plugin-publish")

@Suppress("UnstableApiUsage")
the<GradlePluginDevelopmentExtension>().apply {
    website.set("https://kotlinlang.org")
    vcsUrl.set("https://github.com/Kotlin/kotlinx-rpc")

    plugins.all {
        tags.set(
            setOf(
                "kotlin",
                "kotlinx",
                "rpc",
                "web",
                "services",
                "asynchronous",
                "network",
            )
        )
    }
}

val setupPluginUploadFromEnvironment = tasks.register("setupPluginUploadFromEnvironment") {
    doLast {
        val key = System.getenv("GRADLE_PUBLISH_KEY")
        val secret = System.getenv("GRADLE_PUBLISH_SECRET")

        if (key == null || secret == null) {
            throw GradleException(
                "GRADLE_PUBLISH_KEY and/or GRADLE_PUBLISH_SECRET are not defined environment variables"
            )
        }

        System.setProperty("gradle.publish.key", key)
        System.setProperty("gradle.publish.secret", secret)
    }
}

tasks.named("publishPlugins") {
    dependsOn(setupPluginUploadFromEnvironment)
}
