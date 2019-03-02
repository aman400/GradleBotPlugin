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
                    it.userConfig = extension.userConfig
                    it.config = extension.config
                }
                val flavoursTask = create("getProductFlavours", ProductFlavoursTask::class.java) {
                    it.userConfig = extension.userConfig
                    it.config = extension.config
                }
                create("fetchRemoteBranches", FetchRemoteBranchesTask::class.java) {
                    it.userConfig = extension.userConfig
                    it.config = extension.config
                }
                val pullCodeTask = create("pullCode", PullCodeTask::class.java) {
                    it.gitConfig = extension.userConfig.git
                }
                val cleanOutputTask = create("cleanOutput", CleanOutputTask::class.java)

                create("resetBranch", ResetBranchTask::class.java) {
                    it.config = extension.userConfig.git
                }

                val assembleWithArgsTask = create("assembleWithArgs", AssembleWithArgsTask::class.java) {
                    it.pullCodeTask = pullCodeTask
                    it.userConfig = extension.userConfig
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