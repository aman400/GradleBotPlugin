import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    groovy
    java
    `java-gradle-plugin`
    kotlin("jvm") version "1.3.0"
}

group = "gradlebotplugin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compile(localGroovy())
    compile(kotlin("stdlib-jdk8"))
    compile(gradleApi())
    compile("com.android.tools.build:gradle:1.3.0")
    implementation(group="org.eclipse.jgit", name="org.eclipse.jgit", version = "5.1.3.201810200350-r")
    testCompile("junit", "junit", "4.12")
}

gradlePlugin {
    plugins {
        create("gradle-bot-plugin") {
            id = "com.gradlebot"
            implementationClass = "com.gradlebot.GradleBotPlugin"
        }
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

apply(from = "gradle-mvn-publish.gradle")
