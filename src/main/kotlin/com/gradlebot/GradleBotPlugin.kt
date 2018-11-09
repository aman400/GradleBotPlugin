package com.gradlebot

import com.gradlebot.tasks.AssembleWithArgsTask
import com.gradlebot.tasks.CleanOutputTask
import com.gradlebot.tasks.PullCodeTask
import org.gradle.api.Plugin
import org.gradle.api.Project

open class GradleBotPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.run {
            create("bot", GradleBotExtension::class.java, project)
        }

        with(project.tasks) {
            val pullCodeTask = create("pullCode", PullCodeTask::class.java) {
                it.credentialProvider = extension.credentials
                it.config = extension.config
            }
            val cleanOutputTask = create("cleanOutput", CleanOutputTask::class.java)

            create("assembleWithArgs", AssembleWithArgsTask::class.java) {
                it.credentialProvider = extension.credentials
                it.pullCodeTask = pullCodeTask
                it.config = extension.config
                it.cleanOutputTask = cleanOutputTask
            }
        }
    }
}