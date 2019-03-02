package com.gradlebot.models

import com.gradlebot.extensions.initRepository
import org.gradle.api.Action
import org.gradle.api.Project
import javax.inject.Inject

open class UserConfig @Inject constructor(var project: Project) {

    // Specify the buildType(debug, release.. etc.)
    var buildType: String? = null

    // Specify flavour for the build if any
    var flavour: String? = null

    // Copies the generated app to destinationPath
    var destinationPath: String? = null

    // filePrefix is appended before the file name
    var filePrefix: String? = null

    // Git credentials and userConfig
    val git: GitConfig = project.objects.newInstance(GitConfig::class.java, project)

    fun git(action: Action<GitConfig>) {
        action.execute(git)
        if (git.remote == null) {
            project.initRepository()?.let {
                val storedConfig = it.config
                val remotes = storedConfig.getSubsections("remote")
                git.remote = remotes.firstOrNull() ?: "origin"
            }
        }
    }

}