import com.github.benmanes.gradle.versions.updates.*
import com.vanniktech.maven.publish.SonatypeHost.Companion.CENTRAL_PORTAL

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.ben.manes.versions)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.dokka)
    alias(libs.plugins.vanniktech.maven.publish)
    signing
}

description = "Additional functionality of Ktorm PostgreSQL."
group = "io.github.kodepix"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    api(libs.ktorm.core)
    api(libs.ktorm.support.postgresql)
    api(libs.hikari)
    api(libs.kodepix.commons)
}

kotlin { jvmToolchain(21) }

ktlint {
    verbose = true
    outputToConsole = true
}

mavenPublishing {
    publishToMavenCentral(CENTRAL_PORTAL, automaticRelease = true)
    signAllPublications()

    pom {
        name = "Ktorm PostgreSQL Extras Library"
        description = project.description
        inceptionYear = "2025"
        url = "https://github.com/kodepix/ktorm-postgresql-extras/"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "kodepix"
                name = "kodepix"
                url = "https://github.com/kodepix/"
            }
        }
        scm {
            url = "https://github.com/kodepix/ktorm-postgresql-extras/"
            connection = "scm:git:git://github.com/kodepix/ktorm-postgresql-extras.git"
            developerConnection = "scm:git:git://github.com/kodepix/ktorm-postgresql-extras.git"
        }
    }
}

signing {
    // Used while error "invalid header encountered" is not fixed (https://github.com/vanniktech/gradle-maven-publish-plugin/issues/900)
    val signingPassword: String? by project
    val signingSecretKeyRingFile: String? by project
    useInMemoryPgpKeys(files(signingSecretKeyRingFile).single().readText(), signingPassword)
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
