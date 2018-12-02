package com.gradlebot.models

import org.gradle.api.Action
import org.gradle.api.Project
import javax.inject.Inject

open class Config @Inject constructor(project: Project) {
    var buildType: String? = null
    var flavour: String? = null
    var destinationPath: String? = null
    var filePrefix: String? = null
    var separator: String? = null
    val git: GitConfig = project.objects.newInstance(GitConfig::class.java, project)

    fun git(action: Action<GitConfig>) {
        action.execute(git)
        if (git.remote == null) git.remote = "origin"
    }

}