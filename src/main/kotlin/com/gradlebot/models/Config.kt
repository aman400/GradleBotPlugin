package com.gradlebot.models

import com.gradlebot.extensions.getProjectProperties
import org.gradle.api.Project

open class Config(project: Project) {
    private val properties = project.getProjectProperties()
    val separator = if (properties.containsKey("OUTPUT_SEPARATOR")) {
        properties["OUTPUT_SEPARATOR"]
    } else {
        "<========>"
    }
}