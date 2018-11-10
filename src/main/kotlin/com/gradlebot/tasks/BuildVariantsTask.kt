package com.gradlebot.tasks

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.BuildType
import com.gradlebot.extensions.isAndroidProject
import com.gradlebot.models.Config
import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class BuildVariantsTask: DefaultTask() {
    private var buildTypes: NamedDomainObjectContainer<BuildType>? = null
    @Input
    var config: Config? = null

    init {
        val hasSubProjects = project.subprojects.size > 0

        if (hasSubProjects) {
            project.subprojects.forEach { subProject ->
                subProject.afterEvaluate {
                    if (it.isAndroidProject()) {
                        buildTypes = it.project.extensions.findByType(BaseExtension::class.java)?.buildTypes
                    }
                }
            }
        }
    }

    @TaskAction
    fun flavours() {
        println("Build Variants")
        println(config?.separator)
        buildTypes?.forEach {
            println(it.name)
        }
        println(config?.separator)
    }

    @Input
    override fun getGroup(): String? {
        return "Config"
    }

    @Input
    override fun getDescription(): String? {
        return "Get all the Android project build types"
    }
}