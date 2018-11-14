package com.gradlebot.tasks

import com.gradlebot.extensions.deleteDirectory
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

open class CleanOutputTask : BaseAndroidTask() {

    private var buildDir: String? = null

    @TaskAction
    fun clean() {
        File("$buildDir/outputs/apk").deleteDirectory()
    }

    @Input
    override fun getGroup(): String? {
        return "Clean"
    }

    @Input
    override fun getDescription(): String? {
        return "Clean output apk directory for Android Project"
    }

    override fun evaluateTask() {
        buildDir = androidProject?.buildDir?.path
    }
}