// Top-level build file where you can add configuration options common to all sub-projects/modules.
//buildscript {
//    repositories {
//        google()
//        mavenCentral()
//        //gradlePluginPortal()
//    }
//}
//allprojects {
//    repositories {
//        //google()
//        mavenCentral()
//    }
//}


plugins {
//    alias(libs.plugins.android.application) apply false
//    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.spotless)
    alias(libs.plugins.openApiGenerator)
}
spotless {
    kotlin {
        target(listOf("**/*.kt")) // Wszystkie pliki Kotlin
        targetExclude(listOf("**/generated/**/*.kt")) // Wyklucz pliki wygenerowane
        ktlint(libs.versions.ktlin.get()) // Najnowsza wersja KtLint
        trimTrailingWhitespace() // Usuwanie spacji na końcu linii
        endWithNewline() // Wymuszanie nowej linii na końcu plików
    }
    kotlinGradle {
        target(listOf("**/*.gradle.kts")) // Pliki Gradle w Kotlin DSL
        ktlint(libs.versions.ktlin.get())
    }
}
openApiGenerate {
    val apiModuleName = "web"
    // Set date and time library for project
    // java8 - native java library requires min sdk 26
    // threetenbp - backport from java8, can be used on lower sdk levels
    // string - represent date and time as strings - no sdk requirement
    val dateLibrary = "threetenbp"
    // Set collection type for project
    // array - all collections will be represented as kotlin array
    // list - all collections will be represented as kotlin list
    val collectionType = "list"
    // Set flag to parcelize models
    val parcelizeModels = "true"
    generatorName.set("kotlin")
    // Api configuration file(OpenApi Standard)
    //inputSpec.set("http://localhost:5001/swagger/v1/swagger.json")
    inputSpec.set("$rootDir/specs/openapi.json")
    outputDir.set("$rootDir/$apiModuleName")
    // Api files templates
    //templateDir.set("$rootDir/templates/kotlin-client")
    apiPackage.set("pl.bartpos24.$apiModuleName.api")
    invokerPackage.set("pl.bartpos24.$apiModuleName")
    modelPackage.set("pl.bartpos24.$apiModuleName.model")
    generateModelDocumentation.set(false)
    configOptions.set(
        mapOf(
            "dateLibrary" to dateLibrary,
            "collectionType" to collectionType,
            "parcelizeModels" to parcelizeModels
        )
    )
}
//sourceSets {
//    main {
//        java {
//            srcDir("$rootDir/web/src")
//        }
//    }
//}
