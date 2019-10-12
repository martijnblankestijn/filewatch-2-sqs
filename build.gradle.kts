import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.3.50"
    id("com.github.johnrengelman.shadow") version("5.1.0")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.amazonaws:aws-java-sdk-sqs:1.11.420")
    implementation("org.bouncycastle:bcprov-jdk15on:1.64")
    implementation("org.bouncycastle:bctls-jdk15on:1.64")
}

group = "nl.mblankestijn.demo.sqs"
version =  "1.0-SNAPSHOT"

application {
    mainClassName = "nl.mblankestijn.demo.sqs.SQSSenderKt"
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}