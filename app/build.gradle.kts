plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize)
    //alias("androidx.navigation.safeargs.kotlin")
    alias(libs.plugins.androidNavigationSafeArgs)
}

buildscript {
    dependencies {
        classpath(libs.androidx.navigation.safe.args.gradle.plugin)
        //classpath(libs.libraries.androidx.navigation.safeargs.gradle.plugin)
    }
}

android {
    namespace = "pl.bartpos24.shopmobile"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "pl.bartpos24.shopmobile"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    android {
        signingConfigs {
            create("release") {
                storeFile = file("$rootDir/ShopMobile.jks")
                storePassword = "twoje-haslo-keystore"
                keyAlias = "my-key-alias"
                keyPassword = "twoje-haslo-klucza"
            }
        }

        buildTypes {
            release {
                signingConfig = signingConfigs.getByName("release")
                isMinifyEnabled = false
                proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            }
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
    sourceSets {
        getByName("main") {
            java.srcDir("${rootDir}/generated/openapi/src/main/kotlin")
        }
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
    //openApiGenerate
    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)

    // Moshi
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.moshi.adapters)

    // OkHttp (opcjonalne)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.okhttp.brotli)

    //dagger
    implementation(libs.dagger)
    implementation(libs.daggerAndroid)
    //implementation(libs.daggerAndroidProcessor)
    //compileOnly(libs.daggerAssistedInje)
    implementation(libs.daggerAndroidSupport)
    implementation(libs.work.runtime)
    //daggerAssistedInjectAnnotations
    kapt(libs.daggerCompiler)
    kapt(libs.daggerAndroidProcessor)
    //daggerAssistedInjectProcessor

    implementation(libs.flow.preferences)
    implementation(libs.threetenbp)
    implementation(libs.timber)

    //JWT
    implementation(libs.jwt)

    implementation(libs.corbind)
    implementation(libs.corbindAppCompat)
    implementation(libs.corbindDrawerLayout)
    implementation(libs.corbindMaterial)
    implementation(libs.corbindNavigation)
    implementation(libs.corbindRecyclerView)
    implementation(libs.corbindSwipeRefreshLayout)

    implementation(libs.kotlinx.metadata)

    //Camera and Barcode Scanner
    implementation(libs.cameraCamera2)
    implementation(libs.cameraLifecycle)
    implementation(libs.cameraView)
    implementation(libs.mlKitBarcode)
}
