import java.net.URL

// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.spotlessWithVersion)
    alias(libs.plugins.openApiGenerator)
    //alias(libs.plugins.spotless)
    //alias(libs.plugins.openApiGenerator)
}
spotless {
    kotlin {
        target("**/*.kt") // Wszystkie pliki Kotlin
        targetExclude(listOf("**/generated/**/*.kt")) // Wyklucz pliki wygenerowane
        ktlint(libs.versions.ktlin.get()) // Najnowsza wersja KtLint
        trimTrailingWhitespace() // Usuwanie spacji na końcu linii
        endWithNewline() // Wymuszanie nowej linii na końcu plików
    }
    kotlinGradle {
        target("**/*.gradle.kts") // Pliki Gradle w Kotlin DSL
        ktlint(libs.versions.ktlin.get())
    }
}

openApiGenerate {
    println("Uruchamianie openApiGenerate...")
    generatorName.set("kotlin")
    inputSpec.set("$rootDir/specs/openapi.json")
    outputDir.set("$rootDir/web")
    //invokerPackage.set("pl.bartpos24..web")
    apiPackage.set("pl.bartpos24.web.api")
    modelPackage.set("pl.bartpos24.web.model")
    templateDir.set("$rootDir/templates/kotlin-client")
    configOptions.set(mapOf(
        "dateLibrary" to "threetenbp",
        "collectionType" to "list",
        "parcelizeModels" to "true",
        //"serializationLibrary" to "gson"
    ))
    // Wyłącz generowanie niepotrzebnych plików
    additionalProperties.set(mapOf(
        "gradleBuildFile" to "false",
        //"useSpringBoot3" to "false",
        // "artifactId" to "web",
        // "hideGenerationTimestamp" to "true",
        "generateApiTests" to "false",
        "generateModelTests" to "false",
        //"generateApiDocumentation" to "true",
        "generateModelDocumentation" to "false"
    ))
//    // Wyłącz generowanie plików projektu
    globalProperties.set(mapOf(
        //"supportingFiles" to "true",
        "modelDocs" to "false",
        "apiTests" to "false",
        "modelTests" to "false",
    ))
    println("... koniec openApiGenerate")
}

tasks.register("generateApi") {
    println("Uruchamianie generateApi...")
    dependsOn("openApiGenerate")
    println("... koniec generateApi")
}

tasks.register("downloadApiSchema") {
    group = "openapi"
    description = "Pobiera schemat OpenAPI i generuje API"

    doFirst {
        println("Uruchamianie downloadApiSchema...")
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

        println("... koniec downloadApiSchema")
    }
}
tasks.register("cleanGeneratedOpenApiFiles") {
    group = "openapi"
    description = "Czyści wygenerowane pliki OpenAPI"

    doFirst {
        println("Uruchamianie cleanGeneratedOpenApiFiles ...")
        delete(
            "$rootDir/web/build.gradle",
            "$rootDir/web/settings.gradle",
            "$rootDir/web/gradlew",
            "$rootDir/web/gradlew.bat",
            "$rootDir/web/gradle"
        )
        println("Usunięto wygenerowane pliki z katalogu web")
    }
}
