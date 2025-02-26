import java.net.URL

// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
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
    if (file("$rootDir/$apiModuleName/build.gradle.kts").exists()) {
        globalProperties.set(
            mapOf(
                "supportingFiles" to "false", // **Nie generuje plików build.gradle i settings.gradle**
                "gradleBuildFile" to "false",
                "generateGradleProject" to "false"
            )
        )
    }
}

tasks.register("generateApi") {
    group = "openapi"
    description = "Pobiera schemat OpenAPI i generuje API"

    doFirst {
        val openApiUrl = "http://localhost:5001/swagger/v1/swagger.json"
        val outputFile = File(rootDir, "specs/openapi.json")

        println("Pobieranie OpenAPI schema z: $openApiUrl")

        try {
            val schemaContent = URL(openApiUrl).readText()
            outputFile.parentFile.mkdirs() // Tworzy katalog, jeśli nie istnieje
            outputFile.writeText(schemaContent)

            println("Zapisano OpenAPI schema do: ${outputFile.absolutePath}")
        } catch (e: Exception) {
            println("Błąd pobierania OpenAPI: ${e.message}")
            throw e
        }
        // Uruchomienie openApiGenerate
    }
    println("Uruchamianie openApiGenerate...")
    dependsOn("openApiGenerate")
    println("Formatowanie spotlessKotlinApply...")
    finalizedBy("spotlessKotlinApply")
}
tasks.named("spotlessKotlinApply") {
    mustRunAfter("openApiGenerate")
}
