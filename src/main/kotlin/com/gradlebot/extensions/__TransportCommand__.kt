package com.gradlebot.extensions

import com.gradlebot.auth.CredentialProvider
import com.gradlebot.jgit.SshTransportConfigCallback
import org.eclipse.jgit.api.GitCommand
import org.eclipse.jgit.api.TransportCommand
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider


fun <C : GitCommand<*>?, T> TransportCommand<C, T>.authenticate(credentialProvider: CredentialProvider): TransportCommand<C, T> {
    if (!credentialProvider.isPresent()) {
        println("Either set username and password or setup ssh with following block\nbot {\n    credentials {\n        username = \"<username>\"\n        password = \"<password>\"\n        sshFilePath = \"<ssh file path>\"\n        passphrase = \"<passphrase>\"\n    }\n}")
    }

    if (!credentialProvider.username.isNullOrEmpty()) {
        setCredentialsProvider(
            UsernamePasswordCredentialsProvider(
                credentialProvider.username,
                credentialProvider.password
            )
        )
    } else {
        setTransportConfigCallback(
            SshTransportConfigCallback(
                credentialProvider.sshFilePath,
                credentialProvider.passphrase
            )
        )
    }

    return this
}