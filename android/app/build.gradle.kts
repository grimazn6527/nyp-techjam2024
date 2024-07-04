plugins {
    alias(libs.plugins.android.application)
    id("com.chaquo.python")
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    flavorDimensions += "pyVersion"
    productFlavors {
        create("py311") { dimension = "pyVersion" }
    }

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        ndk {
            // On Apple silicon, you can omit x86_64.
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildToolsVersion = "34.0.0"
}

chaquopy {
    productFlavors {
        getByName("py311") { version = "3.11" }
    }

    defaultConfig{
        pip{
            install("nltk")
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}