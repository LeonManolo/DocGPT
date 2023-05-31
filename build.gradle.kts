plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.7.20"
    id("org.jetbrains.intellij") version "1.10.1"
}

group = "com.manolo_stiller"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val ktor_version = "2.3.0"

    implementation("com.aallam.openai:openai-client:3.2.4") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation("io.ktor:ktor-client-apache5:$ktor_version") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

}



// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.1.4")
    type.set("IC") // Target IDE Platform

    // https://plugins.jetbrains.com/docs/intellij/plugin-dependencies.html#preparing-sandbox
    plugins.set(listOf(
        "com.intellij.java",
        "org.jetbrains.kotlin",
        "Dart:221.6103.1", // https://plugins.jetbrains.com/plugin/6351-dart/versions
        ))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    patchPluginXml {
        sinceBuild.set("221")
        untilBuild.set("231.*")
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