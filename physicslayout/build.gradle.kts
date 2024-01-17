import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.com.vanniktech.publish)
}

android {
    namespace = "com.jawnnypoo.physicslayout"
    compileSdk = 34

    defaultConfig {
        minSdk = 15
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(libs.jbox2d)
    api(libs.translation.drag.view.helper)
}

mavenPublishing {
    configure(
        AndroidSingleVariantLibrary(
            // the published variant
            variant = "release",
            // whether to publish a sources jar
            sourcesJar = true,
            // whether to publish a javadoc jar
            publishJavadocJar = true,
        )
    )
    publishToMavenCentral(SonatypeHost.S01)
    // We need this, because on Jitpack, we don't want the release to be signed,
    // but on GitHub actions, we do, since it will be published to Maven Central
    if (System.getenv("RELEASE_SIGNING_ENABLED") == "true") {
        signAllPublications()
    }
}
