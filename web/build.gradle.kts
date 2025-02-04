plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.maven.publish)
}
android {
    namespace = "pl.bartpos24.web"
    compileSdk = libs.versions.compileSdk.get().toInt()//compileSdkVersion(libs.versions.compileSdk.get().toInt())
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()//minSdkVersion(libs.versions.minSdk.get().toInt())
        targetSdk = libs.versions.targetSdk.get().toInt()//targetSdkVersion(libs.versions.targetSdk.get().toInt())
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            //isMinifyEnabled = false
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
}
dependencies {
    implementation(libs.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.moshi.adapters)
    implementation(libs.okhttp)
    implementation(libs.threetenbp)
    testImplementation(libs.kotlintest.runner.junit5)
}
