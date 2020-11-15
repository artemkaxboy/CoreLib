@file:Suppress("SpellCheckingInspection")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    kotlin("jvm") version "1.4.10"

    // https://docs.gradle.org/current/samples/sample_building_kotlin_libraries.html
    `java-library`
    `maven-publish`
    jacoco

    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
}

val local = Properties().apply {
    rootProject.file("local.properties")
        .takeIf { it.exists() }
        ?.inputStream()
        ?.use { this.load(it) }
}

group = "com.github.artemkaxboy"
version = local.getProperty("application.version") ?:
    System.getenv("RELEASE_VERSION") ?: "local"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // org.assertj:assertj-core:3.6.2
    testImplementation("org.assertj:assertj-core:3.18.0")

    // tests
    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

// https://medium.com/@arunvelsriram/jacoco-configuration-using-gradles-kotlin-dsl-67a8870b1c68
tasks.jacocoTestReport {
    dependsOn("test")

    reports {
        xml.isEnabled = true
        csv.isEnabled = false
        html.isEnabled = false
        html.destination = file("$buildDir/reports/coverage")
    }
}

java {
    withSourcesJar()
}

publishing {

    // https://docs.github.com/en/free-pro-team@latest/actions/guides/publishing-java-packages-with-gradle
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/artemkaxboy/CoreLib")

            credentials {
                username = local.getProperty("github.username") ?: System.getenv("GITHUB_ACTOR")
                password = local.getProperty("github.token") ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }

    // https://docs.gradle.org/current/userguide/publishing_maven.html
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
