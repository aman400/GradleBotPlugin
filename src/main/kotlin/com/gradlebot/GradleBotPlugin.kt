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
            val assembleWithArgsTask = create("assembleWithArgs", AssembleWithArgsTask::class.java) {
                it.credentialProvider = extension.credentialProvider
            }

            val pullCodeTask = create("pullCode", PullCodeTask::class.java) {
                it.credentialProvider = extension.credentialProvider
                it.branch = extension.branch
            }

            val hasSubProjects = project.subprojects.size > 0

            if (hasSubProjects) {
                project.subprojects.forEach { subProject ->
                    subProject.afterEvaluate {
                        println("Evaluation done $it")
                        if(isAndroidApplication(it)) {
                            it.tasks.forEach {
                                println(it.name)
                            }
                            val assembleTask = it.tasks.findByName(BasePlugin.ASSEMBLE_TASK_NAME)
                            val cleanTask = it.tasks.findByPath(BasePlugin.CLEAN_TASK_NAME)
                            println(assembleTask?.name)
                            println(cleanTask?.name)
                            assembleWithArgsTask.dependsOn(assembleTask)
                            assembleWithArgsTask.dependsOn(cleanTask)
                            assembleWithArgsTask.dependsOn(pullCodeTask)
                            cleanTask?.mustRunAfter(pullCodeTask)
                            assembleWithArgsTask.finalizedBy(assembleTask)
                        }
                    }
                }
            }
        }
    }

    private fun isAndroidApplication(project: Project) : Boolean {
        return project.plugins.hasPlugin("com.android.application")
    }
}