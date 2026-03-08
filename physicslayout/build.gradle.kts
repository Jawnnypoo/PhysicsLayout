import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.vanniktech.publish)
}

group = findProperty("GROUP") as String
version = findProperty("VERSION_NAME") as String

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

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(libs.jbox2d)
    api(libs.translation.drag.view.helper)
}

mavenPublishing {
    configure(AndroidSingleVariantLibrary("release", true, true))
    coordinates("com.jawnnypoo", "physicslayout", version.toString())
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    if (System.getenv("RELEASE_SIGNING_ENABLED") == "true") {
        signAllPublications()
    }
}
