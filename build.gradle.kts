import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val logback_version = "1.5.3"
val ktor_version = "2.3.12"

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.0.0"
    id("org.jetbrains.intellij") version "1.17.3"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
}

group = "com.manolo_stiller"
version = "0.0.2"


repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
}

configurations.all {
    exclude(group = "org.slf4j", module = "slf4j-api")
}

dependencies {


    implementation("ch.qos.logback:logback-classic:$logback_version")

    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")

//    implementation("com.aallam.openai:openai-client:3.7.2") {
//        exclude(group = "org.slf4j", module = "slf4j-api")
//        // Prevents java.lang.LinkageError: java.lang.LinkageError: loader constraint violation:when resolving method 'long kotlin.time.Duration.toLong-impl(long, kotlin.time.DurationUnit)'
//        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
//    }
//    implementation("io.ktor:ktor-client-cio:2.3.11") {
//        exclude(group = "org.slf4j", module = "slf4j-api")
//        // Prevents java.lang.LinkageError: java.lang.LinkageError: loader constraint violation: when resolving method 'long kotlin.time.Duration.toLong-impl(long, kotlin.time.DurationUnit)'
//        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
//    }

}


// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2024.1.4")
    type.set("IC") // Target IDE Platform, IC = IntelliJ Community Edition

    // https://plugins.jetbrains.com/docs/intellij/plugin-dependencies.html#preparing-sandbox
    plugins.set(
        listOf(
            "com.intellij.java",
            "org.jetbrains.kotlin",
            "Dart:241.15845", // https://plugins.jetbrains.com/plugin/6351-dart/versions
        )
    )
}



tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            freeCompilerArgs = listOf("-Xjvm-default=all")
        }
    }

    patchPluginXml {
        version.set("${project.version}")
        sinceBuild.set("241") // https://stackoverflow.com/questions/73191608/how-do-intellij-version-and-sincebuild-untilbuild-relate-to-each-other
        //untilBuild.set("242.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
