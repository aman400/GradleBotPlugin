package com.gradlebot.tasks

import com.gradlebot.extensions.isAndroidProject
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Internal

abstract class BaseAndroidTask: DefaultTask() {

    @Internal
    var androidProject: Project? = null

    init {
        if(project.isAndroidProject()) {
            androidProject = project
        } else if (project.subprojects.size > 0) {
            project.subprojects.forEach { subProject ->
                subProject.afterEvaluate {
                    if (it.isAndroidProject()) {
                        androidProject = it
                    }
                }
            }
        }
    }

    /**
     * Evaluate android task and setup variables
     */
    abstract fun evaluateTask()
}