package com.gradlebot

import com.gradlebot.extensions.getProjectProperties
import com.gradlebot.extensions.initRepository
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
        val projectProperties = project.getProjectProperties()

        if(!userConfig.git.credentials.isPresent()) {
            if (projectProperties.containsKey("git.ssh.path")) {
                userConfig.git.credentials.sshFilePath = projectProperties.getProperty("git.ssh.path")
            }

            if (projectProperties.containsKey("git.ssh.passphrase")) {
                userConfig.git.credentials.passphrase = projectProperties.getProperty("git.ssh.passphrase")
            }

            if (projectProperties.containsKey("git.username")) {
                userConfig.git.credentials.username = projectProperties.getProperty("git.username")
            }

            if (projectProperties.containsKey("git.password")) {
                userConfig.git.credentials.password = projectProperties.getProperty("git.password")
            }
        }

        if(userConfig.git.remote == null) {
            if(projectProperties.containsKey("git.remote")) {
                userConfig.git.remote = projectProperties.getProperty("git.remote")
            } else {
                project.initRepository()?.let {
                    val storedConfig = it.config
                    val remotes = storedConfig.getSubsections("remote")
                    userConfig.git.remote = remotes.firstOrNull() ?: "origin"
                }
            }
        }
        if(userConfig.git.defaultBranch == null) {
            userConfig.git.defaultBranch = if(projectProperties.containsKey("git.branch.default")) {
                projectProperties.getProperty("git.branch.default")
            } else {
                "master"
            }
        }
    }
}