package com.gradlebot.tasks

import com.gradlebot.auth.CredentialProvider
import com.gradlebot.models.Config
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.lang.NullPointerException
import java.util.concurrent.ExecutionException

@CacheableTask
open class AssembleWithArgsTask : DefaultTask() {
    lateinit var credentialProvider: CredentialProvider
    lateinit var pullCodeTask: PullCodeTask
    var config: Config? = null

    @Internal
    var androidBuildDir: String? = null

    init {
        val hasSubProjects = project.subprojects.size > 0

        if (hasSubProjects) {
            project.subprojects.forEach { subProject ->
                subProject.afterEvaluate {
                    if (isAndroidApplication(it)) {
                        androidBuildDir = it.project.buildDir.path
                        val assembleTask = it.tasks.findByName(BasePlugin.ASSEMBLE_TASK_NAME)
                        val cleanTask = it.tasks.findByPath(BasePlugin.CLEAN_TASK_NAME)
                        dependsOn(assembleTask)
                        dependsOn(cleanTask)
                        dependsOn(pullCodeTask)
                        cleanTask?.mustRunAfter(pullCodeTask)
                        assembleTask?.mustRunAfter(cleanTask)

                    }
                }
            }
        }
    }

    @TaskAction
    fun helloWorld() {
//        def buildType = project.hasProperty('BUILD_TYPE') ? BUILD_TYPE : DEFAULT_BUILD_TYPE
//        def flavourType = project.hasProperty('FLAVOUR') ? FLAVOUR : null
//        def path = ""
//
//        if (flavourType != null) {
//            path = path + "${flavourType}/"
//        }
//
//        path = path + "${buildType}/"
//        from file(path)
//        include "*.apk"
//        into file(destination_path)
//        if (fileTree(path).files.size() == 0) throw new GradleException("Invalid BUILD_TYPE or FLAVOUR")
//        println "Moving files from ${path} to ${destination_path}"
        if (config != null && config?.destinationPath != null && config?.buildType != null) {
            project.copy {
                val sourceDir =
                    File("$androidBuildDir/outputs/apk/${config?.flavour?.let { "${config?.flavour}/" } ?: ""}${config?.buildType}/")
                println(sourceDir.path)
                it.from(sourceDir)
                it.include("*.apk")
                it.into(File(config?.destinationPath!!))
                if (sourceDir.listFiles() == null || sourceDir.listFiles().firstOrNull { file ->
                        file.name.contains("apk")
                    } == null) {
                    throw ExecutionException(NullPointerException("Invalid buildType or flavour"))
                }
                print("Moved file from ${sourceDir.path} to ${config?.destinationPath!!}")
            }
        } else {
            println("destination path or buildType is missing")
        }
    }

    override fun getGroup(): String? {
        return "Assemble"
    }

    override fun getDescription(): String? {
        return "Pass command line arguments to assemble task of android"
    }

    private fun isAndroidApplication(project: Project): Boolean {
        return project.plugins.hasPlugin("com.android.application")
    }
}