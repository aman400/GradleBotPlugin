package com.gradlebot

import com.gradlebot.auth.CredentialProvider
import com.gradlebot.models.Config
import org.gradle.api.Action
import org.gradle.api.Project
import javax.inject.Inject

open class GradleBotExtension @Inject constructor(val project: Project) {
    val credentials: CredentialProvider = project.objects.newInstance(CredentialProvider::class.java)
    val config: Config = project.objects.newInstance(Config::class.java)

    fun credentials(action: Action<CredentialProvider>) {
        action.execute(credentials)
    }

    fun config(action: Action<Config>) {
        action.execute(config)
    }
}