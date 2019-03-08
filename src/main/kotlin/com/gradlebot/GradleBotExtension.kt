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

        if (!userConfig.git.credentials.isPresent()) {
            if (projectProperties.containsKey("gradlebot.git.ssh.path")) {
                userConfig.git.credentials.sshFilePath = projectProperties.getProperty("gradlebot.git.ssh.path")
            }

            if (projectProperties.containsKey("gradlebot.git.ssh.passphrase")) {
                userConfig.git.credentials.passphrase = projectProperties.getProperty("gradlebot.git.ssh.passphrase")
            }

            if (projectProperties.containsKey("gradlebot.git.username")) {
                userConfig.git.credentials.username = projectProperties.getProperty("gradlebot.git.username")
            }

            if (projectProperties.containsKey("gradlebot.git.password")) {
                userConfig.git.credentials.password = projectProperties.getProperty("gradlebot.git.password")
            }
        }

        if (userConfig.git.remote == null) {
            if (projectProperties.containsKey("gradlebot.git.remote")) {
                userConfig.git.remote = projectProperties.getProperty("gradlebot.git.remote")
            } else {
                project.initRepository()?.let {
                    val storedConfig = it.config
                    val remotes = storedConfig.getSubsections("remote")
                    userConfig.git.remote = remotes.firstOrNull() ?: "origin"
                }
            }
        }

        if (userConfig.git.branch == null) {
            if (projectProperties.containsKey("gradlebot.branch.name")) {
                userConfig.git.branch = projectProperties.getProperty("gradlebot.branch.name")
            }
        }

        if (userConfig.buildType == null) {
            if (projectProperties.containsKey("gradlebot.build.type")) {
                userConfig.buildType = projectProperties.getProperty("gradlebot.build.type")
            }
        }
        if(userConfig.flavour == null) {
            if(projectProperties.containsKey("gradlebot.build.flavour")) {
                userConfig.flavour = projectProperties.getProperty("gradlebot.build.flavour")
            }
        }
        if(userConfig.filePrefix == null) {
            if(projectProperties.containsKey("gradlebot.file.prefix")) {
                userConfig.filePrefix = projectProperties.getProperty("gradlebot.file.prefix")
            }
        }
        if (userConfig.git.defaultBranch == null) {
            userConfig.git.defaultBranch = if (projectProperties.containsKey("gradlebot.git.branch.default")) {
                projectProperties.getProperty("gradlebot.git.branch.default")
            } else {
                "master"
            }
        }
    }
}