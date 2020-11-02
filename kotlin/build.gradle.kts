import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    kotlin("jvm") version "1.4.10"

    // https://docs.gradle.org/current/samples/sample_building_kotlin_libraries.html
    `java-library`
    `maven-publish`
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
