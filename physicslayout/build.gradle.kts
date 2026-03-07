import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.com.vanniktech.publish)
}

android {
    namespace = "com.jawnnypoo.physicslayout"
    compileSdk = 35

    defaultConfig {
        minSdk = 15
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
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
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    if (System.getenv("RELEASE_SIGNING_ENABLED") == "true") {
        signAllPublications()
    }
}
