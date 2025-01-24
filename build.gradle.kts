// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.spotless)
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
