import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"
    application
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    mavenLocal()
    maven(url = "https://kotlin.bintray.com/kotlinx")
    maven(url = "https://packages.confluent.io/maven/")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.apache.kafka:kafka-clients:2.6.0")
    implementation("org.apache.kafka:kafka-streams:2.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0-RC2")
    implementation("org.jetbrains.kotlinx", "kotlinx-cli", "0.3")
    implementation("org.slf4j:slf4j-simple:1.7.30")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.apache.kafka:kafka-streams-test-utils:2.6.0")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.6.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xinline-classes")
        jvmTarget = "1.8"
    }
}

tasks.test { useJUnitPlatform() }

application {
    mainClassName = "org.example.MainKt"
}

tasks.shadowJar {
    archiveBaseName.set(rootProject.name)
    manifest {
        attributes(mapOf("Implementation-Version" to archiveVersion))
    }
    mergeServiceFiles()
    archiveClassifier.set("")
    archiveVersion.set("")
}
