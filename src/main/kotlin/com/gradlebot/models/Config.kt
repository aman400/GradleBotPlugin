package com.gradlebot.models

import com.gradlebot.extensions.getProjectProperties
import org.gradle.api.Project

open class Config(project: Project) {
    val separator = if (project.hasProperty("OUTPUT_SEPARATOR")) {
        project.properties["OUTPUT_SEPARATOR"]
    } else {
        val projectProperties = project.getProjectProperties()
        if(projectProperties.containsKey("output.separator")) {
            projectProperties.getProperty("output.separator")
        } else {
            "<========>"
        }
    }
}