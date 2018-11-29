package com.gradlebot.tasks

import com.gradlebot.extensions.deleteDirectory
import com.gradlebot.extensions.isAndroidLibrary
import com.gradlebot.extensions.isAndroidProject
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

open class CleanOutputTask : BaseAndroidTask() {

    private var buildDirs: MutableList<String> = mutableListOf()

    @TaskAction
    fun clean() {
        buildDirs.forEach {
            logger.debug("deleting $it/outputs/")
            File("$it/outputs/").deleteDirectory()
        }
    }

    @Input
    override fun getGroup(): String {
        return "Clean"
    }

    @Input
    override fun getDescription(): String {
        return "Clean output apk directory for Android Project"
    }

    override fun evaluateTask() {
        if(project.isAndroidProject()) {
            buildDirs.add(project.buildDir.path)
        }
        if(!project.subprojects.isEmpty()) {
            project.subprojects.forEach {
                if(it.isAndroidProject() || it.isAndroidLibrary()) {
                    buildDirs.add(it.buildDir.path)
                }
            }
        }
    }
}