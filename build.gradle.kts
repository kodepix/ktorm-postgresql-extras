import com.github.benmanes.gradle.versions.updates.*

plugins {
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
    alias(libs.plugins.nl.littlerobots.version.catalog.update)
    alias(libs.plugins.com.github.ben.manes.versions)
}

group = "io.github.kodepix"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin { jvmToolchain(21) }

tasks {
    test { useJUnitPlatform() }

    withType<DependencyUpdatesTask> {
        rejectVersionIf { isNonStable(candidate.version) }
    }
}

private fun isNonStable(version: String) = run {
    val versionIsStable = stableKeywords.any { version.uppercase().contains(it) }
    val isStable = versionIsStable || versionRegex.matches(version)
    !isStable
}

private val stableKeywords = listOf("RELEASE", "FINAL", "GA")
private val versionRegex = Regex("^[0-9,.v-]+(-r)?$")
