package util

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.kotlin(block: KotlinMultiplatformExtension.() -> Unit) {
    configure(block)
}
