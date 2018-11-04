package com.gradlebot

import org.gradle.api.Plugin
import org.gradle.api.Project

open class GradleBotPlugin : Plugin<Project> {
    override fun apply(project: Project) {
//        if (project.plugins.findPlugin("com.android.application") == null) {
//        }
        val extension = project.extensions.run {
            create("gradlebotConfig", GradleBotExtension::class.java, project.objects)
        }
        with(project.tasks) {
            create("assembleWithArgs", AssembleWithArgsTask::class.java, extension.credentialProvider)

            create("pullCode", PullCodeTask::class.java, extension.credentialProvider)
        }
    }
}