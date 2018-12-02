package com.gradlebot

import com.gradlebot.models.Config
import org.gradle.api.Action
import org.gradle.api.Project
import javax.inject.Inject

open class GradleBotExtension @Inject constructor(val project: Project) {
    val config: Config = project.objects.newInstance(Config::class.java, project)

    fun config(action: Action<Config>) {
        action.execute(config)
        if (config.separator == null) config.separator = "<========>"
    }
}