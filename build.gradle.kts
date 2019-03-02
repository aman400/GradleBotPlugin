import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    groovy
    java
    `java-gradle-plugin`
    kotlin("jvm") version "1.3.21"
    id("com.gradle.plugin-publish") version "0.10.1"
}

val POM_ARTIFACT_ID: String by project
val GROUP: String by project
val POM_NAME: String by project
val POM_DESCRIPTION: String by project
val POM_URL: String by project
val POM_SCM_URL: String by project
val VERSION_NAME: String by project

repositories {
    google()
    mavenCentral()
}

version = VERSION_NAME

dependencies {
    implementation(localGroovy())
    implementation(kotlin("stdlib-jdk8"))
    implementation(gradleApi())
    implementation(Config.Deps.androidGradle)
    implementation(Config.Deps.jgit)
    testImplementation(Config.TestDeps.junit)
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
    tags = listOf("Custom Build Automation", "gradlebot", "build",
        "Android", "Build Automation")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

apply(from = "gradle-mvn-publish.gradle")