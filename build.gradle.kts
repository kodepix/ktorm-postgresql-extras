import com.github.benmanes.gradle.versions.updates.*

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.ben.manes.versions)
}

group = "io.github.kodepix"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(libs.kodepix.commons)
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
