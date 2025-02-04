//group 'org.openapitools'
//version '1.0.0'

//wrapper {
//    gradleVersion = '7.5'
//    distributionUrl = "https://services.gradle.org/distributions/gradle-$gradleVersion-all.zip"
//}

//buildscript {
//    ext.kotlin_version = '1.8.10'
//
//    repositories {
//        maven { url "https://repo1.maven.org/maven2" }
//    }
//    dependencies {
//        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
//    }
//}

//apply plugin: 'kotlin'
//apply plugin: 'kotlin-parcelize'
//apply plugin: 'maven-publish'
//androidExtensions {
//    isExperimental = true
//}
//repositories {
//    //mavenCentral()
//    maven { url "https://repo1.maven.org/maven2" }
//}
//
//test {
//    useJUnitPlatform()
//}
plugins {
//    alias(libs.plugins.android.library)
//    alias(libs.plugins.android)
//    alias(libs.plugins.kotlin.kapt)
//
//    alias(libs.plugins.kotlin.jvm) apply false
//    alias(libs.plugins.kotlin.parcelize)
//    alias(libs.plugins.maven.publish)
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
//    sourceSets {
//        getByName("main").java.setSrcDirs(listOf("src/main/java", "src/main/kotlin"))
//    }
//    kotlinOptions {
//        jvmTarget = "11"
//    }
}
repositories {
    //google()
    //mavenCentral()
    //maven(url = "https://repo1.maven.org/maven2")
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
    implementation(libs.kotlinx.parcelize)
}
