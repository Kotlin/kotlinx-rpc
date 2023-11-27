import util.configureJvmPublication
import util.configureKmpPublication
import util.configureKrpcPublication

val publishingExtension = project.extensions.findByType<PublishingExtension>()

if (name.startsWith("krpc")) {
    if (publishingExtension != null) {
        publishingExtension.configureKrpcPublication()
    } else {
        plugins.withId("org.jetbrains.kotlin.jvm") {
            configureJvmPublication()
        }

        plugins.withId("org.jetbrains.kotlin.multiplatform") {
            configureKmpPublication()
        }
    }
}
