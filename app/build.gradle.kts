plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "com.jawnnypoo.physicslayout.sample"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jawnnypoo.physicslayout.sample"
        minSdk = 21
        targetSdk = 34
        versionCode = 101
        versionName = "1.0.1"
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles("proguard-rules.pro", getDefaultProguardFile("proguard-android.txt"))
        }
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles("proguard-rules.pro", getDefaultProguardFile("proguard-android.txt"))
        }
    }

}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.viewmodel)

    implementation(libs.google.material)

    implementation(libs.circle.image.view)

    implementation(libs.coil)

    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)

    implementation(libs.moshi)

    //https://github.com/blazsolar/FlowLayout/issues/31
    implementation(libs.flowlayout) {
        exclude(group = "com.intellij", module = "annotations")
    }


    implementation(libs.gimbal)

    implementation(project(":physicslayout"))
}
