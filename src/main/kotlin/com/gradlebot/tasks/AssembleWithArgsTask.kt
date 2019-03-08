package com.gradlebot.tasks

import com.gradlebot.models.UserConfig
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

    @Internal
    lateinit var pullCodeTask: PullCodeTask
    @Internal
    lateinit var cleanOutputTask: CleanOutputTask
    @Input
    var userConfig: UserConfig? = null

    @TaskAction
    fun assemble() {
        val buildType = userConfig?.buildType ?: defaultBuildType
        val flavour = userConfig?.flavour ?: defaultProductFlavour
        // Move the assembled Apk
        if (userConfig != null && userConfig?.destinationPath != null) {
            project.copy {
                val sourceDir =
                    File("$androidBuildDir${File.separator}outputs${File.separator}apk${File.separator}${flavour?.let { "$flavour${File.separator}" }
                        ?: ""}${buildType?.let { "$buildType${File.separator}" } ?: ""}")
                it.from(sourceDir) { copySpec ->
                    copySpec.include("*.apk")

                    userConfig?.filePrefix?.let { filePrefix ->
                        copySpec.rename { filename ->
                            "$filePrefix-${filename.substringAfter("-")}"
                        }
                    }
                }
                it.into(File(userConfig?.destinationPath!!))
                if (sourceDir.listFiles() == null || sourceDir.listFiles().firstOrNull { file ->
                        file.name.contains("apk")
                    } == null) {
                    throw ExecutionException(NullPointerException("Invalid buildType or flavour"))
                }
                logger.debug("Moved file from ${sourceDir.path} to ${userConfig?.destinationPath!!}")
            }
        } else {
            logger.warn("destination path is missing")
        }
    }

    @Input
    override fun getGroup(): String {
        return "Assemble"
    }

    @Input
    override fun getDescription(): String {
        return "Clean, Assemble, Build and Move APK to given path"
    }

    override fun evaluateTask() {
        val assembleTaskName =
            "${BasePlugin.ASSEMBLE_TASK_NAME}${userConfig?.flavour?.capitalize() ?: (defaultProductFlavour?.capitalize()
                ?: "")}${userConfig?.buildType?.capitalize() ?: (defaultBuildType?.capitalize() ?: "")}"
        val buildTaskName =
            "${BasePlugin.BUILD_GROUP}${userConfig?.flavour?.capitalize() ?: (defaultProductFlavour?.capitalize()
                ?: "")}${userConfig?.buildType?.capitalize() ?: (defaultBuildType?.capitalize() ?: "")}"

        androidProject?.let {
            androidBuildDir = it.buildDir.path
            val assembleTask = it.tasks.findByName(assembleTaskName)
            val buildTask = it.tasks.find { task ->
                task.name.contains(buildTaskName)
            }

            dependsOn(buildTask)
            dependsOn(assembleTask)
            dependsOn(cleanOutputTask)

            buildTask?.mustRunAfter(cleanOutputTask)
            assembleTask?.mustRunAfter(buildTask)
        }
    }
}