plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.jawnnypoo.physicslayout.sample"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jawnnypoo.physicslayout.sample"
        minSdk = 21
        targetSdk = 35
        versionCode = 102
        versionName = "1.0.2"
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles("proguard-rules.pro", getDefaultProguardFile("proguard-android-optimize.txt"))
        }
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles("proguard-rules.pro", getDefaultProguardFile("proguard-android-optimize.txt"))
        }
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
