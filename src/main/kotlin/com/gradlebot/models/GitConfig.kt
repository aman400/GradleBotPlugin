package com.gradlebot.models

import com.gradlebot.auth.CredentialProvider
import org.gradle.api.Action
import org.gradle.api.Project
import javax.inject.Inject

open class GitConfig @Inject constructor(project: Project) {

    // specify the remote(eg. origin)
    var remote: String? = null

    // git branch to build the app on
    var branch: String? = null

    // default branch that is checked out to after build
    var defaultBranch: String? = null

    // git remote credentials
    val credentials: CredentialProvider = project.objects.newInstance(CredentialProvider::class.java)

    fun credentials(action: Action<CredentialProvider>) {
        action.execute(credentials)
    }
}