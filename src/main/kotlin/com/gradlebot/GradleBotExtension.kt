package com.gradlebot

import com.gradlebot.auth.CredentialProvider
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

open class GradleBotExtension @Inject constructor(project: Project) {
    var credentialProvider: CredentialProvider = project.objects.newInstance(CredentialProvider::class.java)

    val branch: Property<String?>? = project.objects.property(String::class.java)

    fun credentials(action: Action<CredentialProvider>) {
        action.execute(credentialProvider)
    }
}