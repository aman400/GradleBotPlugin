package com.gradlebot.tasks

import com.gradlebot.extensions.deleteDirectory
import com.gradlebot.extensions.isAndroidProject
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

open class CleanOutputTask : DefaultTask() {
    @Input
    private var buildDir: String? = null

    init {
        val hasSubProjects = project.subprojects.size > 0

        if (hasSubProjects) {
            project.subprojects.forEach { subProject ->
                subProject.afterEvaluate {
                    if (it.isAndroidProject()) {
                        buildDir = it.project.buildDir.path
                    }
                }
            }
        }
    }

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
}