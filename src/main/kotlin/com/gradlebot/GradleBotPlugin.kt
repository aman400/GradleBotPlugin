package com.gradlebot

import com.gradlebot.tasks.AssembleWithArgsTask
import com.gradlebot.tasks.PullCodeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin

open class GradleBotPlugin : Plugin<Project> {
    override fun apply(project: Project) {
//        if (project.plugins.findPlugin("com.android.application") == null) {
//        }
        val extension = project.extensions.run {
            create("gradlebotConfig", GradleBotExtension::class.java, project)
        }

        with(project.tasks) {
            val pullCodeTask = create("pullCode", PullCodeTask::class.java) {
                it.credentialProvider = extension.credentialProvider
                it.branch = extension.branch
            }
            val assembleWithArgsTask = create("assembleWithArgs", AssembleWithArgsTask::class.java) {
                it.credentialProvider = extension.credentialProvider
                it.pullCodeTask = pullCodeTask
            }
        }
    }
}