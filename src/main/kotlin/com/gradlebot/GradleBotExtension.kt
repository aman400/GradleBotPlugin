package com.gradlebot

import com.gradlebot.models.Config
import com.gradlebot.models.UserConfig
import org.gradle.api.Action
import org.gradle.api.Project
import javax.inject.Inject

open class GradleBotExtension @Inject constructor(val project: Project) {
    val userConfig: UserConfig = project.objects.newInstance(UserConfig::class.java, project)

    val config: Config = Config(project)

    fun config(action: Action<UserConfig>) {
        action.execute(userConfig)
    }
}