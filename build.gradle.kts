import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    groovy
    java
    `java-gradle-plugin`
    kotlin("jvm") version "1.3.0"
}

group = "BotGradlePlugin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compile(localGroovy())
    compile(kotlin("stdlib-jdk8"))
    compile(gradleApi())
    testCompile("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}