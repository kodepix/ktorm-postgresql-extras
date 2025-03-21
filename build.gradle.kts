import com.github.benmanes.gradle.versions.updates.*

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.ben.manes.versions)
    alias(libs.plugins.ktlint)
}

group = "io.github.kodepix"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(libs.ktorm.core)
    implementation(libs.ktorm.support.postgresql)
    implementation(libs.hikari)
    implementation(libs.kodepix.commons)
}

kotlin { jvmToolchain(21) }


ktlint {
    verbose = true
    outputToConsole = true
}

tasks {
    test { useJUnitPlatform() }

    withType<DependencyUpdatesTask> {
        rejectVersionIf { isNonStable(candidate.version) }
    }

    runKtlintCheckOverKotlinScripts { dependsOn(runKtlintFormatOverKotlinScripts) }
    runKtlintCheckOverMainSourceSet { dependsOn(runKtlintFormatOverMainSourceSet) }
    runKtlintCheckOverTestSourceSet { dependsOn(runKtlintFormatOverTestSourceSet) }
}

private fun isNonStable(version: String) = run {
    val versionIsStable = stableKeywords.any { version.uppercase().contains(it) }
    val isStable = versionIsStable || versionRegex.matches(version)
    !isStable
}

private val stableKeywords = listOf("RELEASE", "FINAL", "GA")
private val versionRegex = Regex("^[0-9,.v-]+(-r)?$")
