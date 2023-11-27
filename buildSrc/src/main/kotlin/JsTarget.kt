import org.gradle.api.*
import org.gradle.kotlin.dsl.*
import java.io.*

internal fun Project.configureJs() {
    configureJsTasks()

    kotlin {
        sourceSets {
            val jsTest by getting {
                dependencies {
                    implementation(npm("puppeteer", "*"))
                }
            }
        }
    }
}

private fun Project.configureJsTasks() {
    kotlin {
        js(IR) {
            nodejs {
                testTask(Action {
                    useMocha {
                        timeout = "10000"
                    }
                })
            }

            browser {
                testTask(Action {
                    useKarma {
                        useChromeHeadless()
                        useConfigDirectory(File(project.rootProject.projectDir, "karma"))
                    }
                })
            }

            binaries.library()
        }
    }
}
