import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    groovy
    java
    `java-gradle-plugin`
    kotlin("jvm") version "1.3.0"
    id("com.gradle.plugin-publish") version "0.10.0"
}

val POM_ARTIFACT_ID: String by project
val GROUP: String by project
val POM_NAME: String by project
val POM_DESCRIPTION: String by project
val POM_URL: String by project
val POM_SCM_URL: String by project

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
        create(POM_ARTIFACT_ID) {
            id = GROUP
            displayName = POM_NAME
            description = POM_DESCRIPTION
            implementationClass = "com.gradlebot.GradleBotPlugin"
        }
    }
}

pluginBundle {
    website = POM_URL
    vcsUrl = POM_SCM_URL
    tags = listOf("custom Build Automation", "gradlebot", "build",
        "Android", "slackbot", "build Automation")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

apply(from = "gradle-mvn-publish.gradle")