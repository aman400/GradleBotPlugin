package com.gradlebot.auth

import javax.inject.Inject

open class CredentialProvider {
    var sshFilePath: String? = null
    var passphrase: String? = null
    var username: String? = null
    var password: String? = null
}