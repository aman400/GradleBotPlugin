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
                val buildVariantsTask = create("getBuildVariants", BuildVariantsTask::class.java) {
                    it.config = extension.config
                }
                val flavoursTask = create("getProductFlavours", ProductFlavoursTask::class.java) {
                    it.config = extension.config
                }
                create("fetchRemoteBranches", FetchRemoteBranchesTask::class.java) {
                    it.config = extension.config
                    it.credentials = extension.credentials
                }
                val pullCodeTask = create("pullCode", PullCodeTask::class.java) {
                    it.credentialProvider = extension.credentials
                    it.config = extension.config
                }
                val cleanOutputTask = create("cleanOutput", CleanOutputTask::class.java)

                val assembleWithArgsTask = create("assembleWithArgs", AssembleWithArgsTask::class.java) {
                    it.credentialProvider = extension.credentials
                    it.pullCodeTask = pullCodeTask
                    it.config = extension.config
                    it.cleanOutputTask = cleanOutputTask
                }

                project.afterEvaluate {
                    buildVariantsTask.evaluateTask()
                    flavoursTask.evaluateTask()
                    cleanOutputTask.evaluateTask()

                    assembleWithArgsTask.defaultBuildType = buildVariantsTask.buildTypes?.firstOrNull()?.name
                    assembleWithArgsTask.defaultProductFlavour = flavoursTask.productFlavours?.firstOrNull()?.name
                    assembleWithArgsTask.evaluateTask()
                }
            }
        }
    }
}