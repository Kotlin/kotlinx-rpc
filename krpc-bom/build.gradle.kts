plugins {
    `java-platform`
    `maven-publish`
    alias(libs.plugins.conventions.common)
}

dependencies {
    constraints {
        rootProject.allprojects.filter {
            it.name.startsWith("krpc") && it != project
        }.forEach { project ->
            api(project)
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("krpcPlatform") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["javaPlatform"])
        }
    }
}
