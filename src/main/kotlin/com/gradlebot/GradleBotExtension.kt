package com.gradlebot

import com.gradlebot.auth.CredentialProvider
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory

open class GradleBotExtension @javax.inject.Inject constructor(objectFactory: ObjectFactory) {
    var credentialProvider: CredentialProvider = objectFactory.newInstance(CredentialProvider::class.java)

    fun credentials(action: Action<CredentialProvider>) {
        action.execute(credentialProvider)
    }
}