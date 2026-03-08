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
        google()
        mavenCentral()
        // TODO: Remove once Gimbal is published to Maven Central
        maven(url = "https://jitpack.io")
    }
}

include(":app", ":physicslayout")
