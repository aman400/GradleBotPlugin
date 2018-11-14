package com.gradlebot

import com.gradlebot.extensions.isAndroidProject
import com.gradlebot.tasks.*
import org.gradle.api.Plugin
import org.gradle.api.Project

open class GradleBotPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.run {
            create("bot", GradleBotExtension::class.java, project)
        }

        val hasAndroidProject = project.isAndroidProject() || (!project.subprojects.isEmpty() && project.subprojects.any {
            it.isAndroidProject()
        })

        if(hasAndroidProject) {
            with(project.tasks) {
                create("getBuildVariants", BuildVariantsTask::class.java) {
                    it.config = extension.config
                    it.evaluateTask()
                }
                create("getProductFlavours", ProductFlavoursTask::class.java) {
                    it.config = extension.config
                    it.evaluateTask()
                }
                create("fetchRemoteBranches", FetchRemoteBranchesTask::class.java) {
                    it.config = extension.config
                }
                val pullCodeTask = create("pullCode", PullCodeTask::class.java) {
                    it.credentialProvider = extension.credentials
                    it.config = extension.config
                }
                val cleanOutputTask = create("cleanOutput", CleanOutputTask::class.java) {
                    it.evaluateTask()
                }

                create("assembleWithArgs", AssembleWithArgsTask::class.java) {
                    it.credentialProvider = extension.credentials
                    it.pullCodeTask = pullCodeTask
                    it.config = extension.config
                    it.cleanOutputTask = cleanOutputTask
                    it.evaluateTask()
                }
            }
        }
    }
}