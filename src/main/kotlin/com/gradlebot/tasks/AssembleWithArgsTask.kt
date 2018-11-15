package com.gradlebot.tasks

import com.gradlebot.auth.CredentialProvider
import com.gradlebot.models.Config
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.concurrent.ExecutionException

@CacheableTask
open class AssembleWithArgsTask : BaseAndroidTask() {
    private var androidBuildDir: String? = null

    @Internal
    var defaultProductFlavour: String? = null
    @Internal
    var defaultBuildType: String? = null

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
        val buildType = config?.buildType ?: defaultBuildType
        val flavour = config?.flavour ?: defaultProductFlavour
        // Move the assembled Apk
        if (config != null && config?.destinationPath != null) {
            project.copy {
                val sourceDir =
                    File("$androidBuildDir/outputs/apk/${flavour?.let { "$flavour/" }
                        ?: ""}${buildType?.let { "$buildType/" } ?: ""}")
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
            println("destination path is missing")
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
        val assembleTaskName =
            "${BasePlugin.ASSEMBLE_TASK_NAME}${config?.flavour?.capitalize() ?: (defaultProductFlavour?.capitalize()
                ?: "")}${config?.buildType?.capitalize() ?: (defaultBuildType?.capitalize() ?: "")}"
        val buildTaskName =
            "${BasePlugin.BUILD_GROUP}${config?.flavour?.capitalize() ?: (defaultProductFlavour?.capitalize()
                ?: "")}${config?.buildType?.capitalize() ?: (defaultBuildType?.capitalize() ?: "")}"

        androidProject?.let {
            androidBuildDir = it.buildDir.path
            val assembleTask = it.tasks.findByName(assembleTaskName)
            val buildTask = it.tasks.find { task ->
                task.name.contains(buildTaskName)
            }

            dependsOn(buildTask)
            dependsOn(assembleTask)
            dependsOn(cleanOutputTask)
            dependsOn(pullCodeTask)

            cleanOutputTask.mustRunAfter(pullCodeTask)
            buildTask?.mustRunAfter(cleanOutputTask)
            assembleTask?.mustRunAfter(buildTask)
        }
    }
}