package com.gradlebot.tasks

import com.gradlebot.auth.CredentialProvider
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction

@CacheableTask
open class AssembleWithArgsTask: DefaultTask() {
    lateinit var credentialProvider: CredentialProvider
    lateinit var pullCodeTask: PullCodeTask

    init {
        val hasSubProjects = project.subprojects.size > 0

        if (hasSubProjects) {
            project.subprojects.forEach { subProject ->
                subProject.afterEvaluate {
                    if(isAndroidApplication(it)) {
                        it.tasks.forEach {
                            println(it.name)
                        }
                        val assembleTask = it.tasks.findByName(BasePlugin.ASSEMBLE_TASK_NAME)
                        val cleanTask = it.tasks.findByPath(BasePlugin.CLEAN_TASK_NAME)
                        dependsOn(assembleTask)
                        dependsOn(cleanTask)
                        dependsOn(pullCodeTask)
                        cleanTask?.mustRunAfter(pullCodeTask)
                        assembleTask?.mustRunAfter(cleanTask)

//                        finalizedBy(assembleTask)
                    }
                }
            }
        }
    }

    @TaskAction
    fun helloWorld() {
        println(credentialProvider.passphrase)
    }

    override fun getGroup(): String? {
        return "Assemble"
    }

    override fun getDescription(): String? {
        return "Pass command line arguments to assemble task of android"
    }

    private fun isAndroidApplication(project: Project) : Boolean {
        return project.plugins.hasPlugin("com.android.application")
    }
}