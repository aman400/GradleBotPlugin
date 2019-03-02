package com.gradlebot.tasks

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.BuildType
import com.gradlebot.models.Config
import com.gradlebot.models.UserConfig
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

open class BuildVariantsTask : BaseAndroidTask() {

    @Internal
    var buildTypes: NamedDomainObjectContainer<BuildType>? = null
    @Input
    var userConfig: UserConfig? = null
    @Input
    var config: Config? = null

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
    override fun getGroup(): String {
        return "Config"
    }

    @Input
    override fun getDescription(): String {
        return "Get all the Android project build types"
    }

    override fun evaluateTask() {
        buildTypes = androidProject?.extensions?.findByType(BaseExtension::class.java)?.buildTypes
    }
}