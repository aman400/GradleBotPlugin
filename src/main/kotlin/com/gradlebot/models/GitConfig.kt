package com.gradlebot.models

import com.gradlebot.auth.CredentialProvider
import org.gradle.api.Action
import org.gradle.api.Project
import javax.inject.Inject

open class GitConfig @Inject constructor(project: Project) {
    var remote: String? = null
    var branch: String? = null
    val credentials: CredentialProvider = project.objects.newInstance(CredentialProvider::class.java)

    fun credentials(action: Action<CredentialProvider>) {
        action.execute(credentials)
    }
}