plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "pl.bartpos24.shopmobile"
    compileSdk = 35 //libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "pl.bartpos24.shopmobile"
        minSdk = 26 //libs.versions.minSdk.get().toInteger()
        targetSdk = 35 //libs.versions.targetSdk.get().toInt()
        versionCode = 1 // libs.versions.versionCode.get().toInt()
        versionName = "1.0" //libs.versions.versionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    implementation(project(":web"))
    //implementation(project("path" to ":web"))
    //openApiGenerate
    // Retrofit
    //implementation(libs.retrofit)
    //implementation(libs.retrofit.converter.moshi)

    // Moshi
    //implementation(libs.moshi)
    //implementation(libs.moshi.kotlin)

    // OkHttp (opcjonalne)
    //implementation(libs.okhttp)

    //implementation(libs.kotlinx.parcelize)
}
