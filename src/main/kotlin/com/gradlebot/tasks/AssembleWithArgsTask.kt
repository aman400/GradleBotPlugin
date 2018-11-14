package com.gradlebot.tasks

import com.gradlebot.auth.CredentialProvider
import com.gradlebot.extensions.isAndroidProject
import com.gradlebot.models.Config
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.*
import java.io.File
import java.lang.NullPointerException
import java.util.concurrent.ExecutionException

@CacheableTask
open class AssembleWithArgsTask : BaseAndroidTask() {
    private var androidBuildDir: String? = null

    @Input
    lateinit var credentialProvider: CredentialProvider
    @Internal
    lateinit var pullCodeTask: PullCodeTask
    @Internal
    lateinit var cleanOutputTask: CleanOutputTask
    @Input
    var config: Config? = null

    @TaskAction
    fun assemble() {
        // Move the assembled Apk
        if (config != null && config?.destinationPath != null && config?.buildType != null) {
            project.copy {
                val sourceDir =
                    File("$androidBuildDir/outputs/apk/${config?.flavour?.let { "${config?.flavour}/" } ?: ""}${config?.buildType}/")
                it.from(sourceDir) { copySpec ->
                    copySpec.include("*.apk")

                    config?.filePrefix?.let { filePrefix ->
                        copySpec.rename { filename ->
                            "$filePrefix-${filename.substringAfter("-")}"
                        }
                    }
                }
                it.into(File(config?.destinationPath!!))
                if (sourceDir.listFiles() == null || sourceDir.listFiles().firstOrNull { file ->
                        file.name.contains("apk")
                    } == null) {
                    throw ExecutionException(NullPointerException("Invalid buildType or flavour"))
                }
                println("Moved file from ${sourceDir.path} to ${config?.destinationPath!!}")
            }
        } else {
            println("destination path or buildType is missing")
        }
    }

    @Input
    override fun getGroup(): String? {
        return "Assemble"
    }

    @Input
    override fun getDescription(): String? {
        return "Clean, Assemble, Build and Move APK to give path"
    }

    override fun evaluateTask() {
        androidProject?.let {
            androidBuildDir = it.buildDir.path
            val assembleTask = it.tasks.findByName(BasePlugin.ASSEMBLE_TASK_NAME)
            dependsOn(assembleTask)
            dependsOn(cleanOutputTask)
            dependsOn(pullCodeTask)
            cleanOutputTask.mustRunAfter(pullCodeTask)
            assembleTask?.mustRunAfter(cleanOutputTask)
        }
    }
}