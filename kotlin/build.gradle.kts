@file:Suppress("SpellCheckingInspection")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    kotlin("jvm") version "1.4.10"

    // https://docs.gradle.org/current/samples/sample_building_kotlin_libraries.html
    `java-library`
    maven
    jacoco

    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
}

group = "com.artemkaxboy.kotlin"
version = System.getenv("RELEASE_VERSION") ?: "0.1-SNAPSHOT"

val local = Properties().apply {
    rootProject.file("local.properties")
        .takeIf { it.exists() }
        ?.inputStream()
        ?.use { this.load(it) }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // org.assertj:assertj-core:3.6.2
    testImplementation("org.assertj:assertj-core:3.18.0")

    // tests
    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    // IDEA needs those:
    testCompileOnly("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testCompileOnly("org.junit.jupiter:junit-jupiter-params:5.7.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
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
