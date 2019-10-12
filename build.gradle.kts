import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.3.50"
    id("com.github.johnrengelman.shadow") version("5.1.0")
    id("com.palantir.graal") version "0.6.0-16-g4a3ef27"
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
graal {
    mainClass(application.mainClassName)
    outputName("filewatch-2-sqs2")
    graalVersion("19.2.0.1")
    option("--no-server")
    option("--no-fallback")
    option("--enable-all-security-services")
    option("--enable-https")
    option("--enable-url-protocols=https")
    option("--report-unsupported-elements-at-runtime")
    option("--initialize-at-build-time=org.bouncycastle.util.Strings")
    option("--initialize-at-build-time=org.apache.http,org.apache.commons.logging,org.apache.commons.codec")
    option("--rerun-class-initialization-at-runtime=org.bouncycastle.crypto.prng.SP800SecureRandom")
    option("--rerun-class-initialization-at-runtime=org.bouncycastle.jcajce.provider.drbg.DRBG\$Default")
    option("--rerun-class-initialization-at-runtime=org.bouncycastle.jcajce.provider.drbg.DRBG\$NonceAndIV")
    option("-J-Djava.security.properties=java.security.overrides")
}