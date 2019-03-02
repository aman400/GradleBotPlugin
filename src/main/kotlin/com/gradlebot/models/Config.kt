package com.gradlebot.models

import org.gradle.api.Project

open class Config(project: Project) {
    val separator = if (project.hasProperty("OUTPUT_SEPARATOR")) {
        project.properties["OUTPUT_SEPARATOR"]
    } else {
        "<========>"
    }
}