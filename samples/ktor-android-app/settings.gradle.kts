pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven(url = "https://maven.pkg.jetbrains.space/public/p/krpc/maven")
        google()
        mavenCentral()
    }
}

rootProject.name = "KtorApplication"
include(":app")
 