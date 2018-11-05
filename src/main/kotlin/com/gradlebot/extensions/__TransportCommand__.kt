package com.gradlebot.extensions

import com.gradlebot.auth.CredentialProvider
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import org.eclipse.jgit.api.GitCommand
import org.eclipse.jgit.api.TransportCommand
import org.eclipse.jgit.api.TransportConfigCallback
import org.eclipse.jgit.transport.*
import org.eclipse.jgit.util.FS
import java.lang.NullPointerException
import java.util.concurrent.ExecutionException

fun <C : GitCommand<*>?, T> TransportCommand<C, T>.authanticate(credentialProvider: CredentialProvider) : TransportCommand<C, T> {
    if(credentialProvider.username.isNullOrEmpty() && credentialProvider.sshFilePath.isNullOrEmpty()) {
        throw ExecutionException(NullPointerException("Either set username and password or setup ssh with following block\ngradlebotConfig {\n    credentials {\n        username = \"<username>\"\n        password = \"<password>\"\n        sshFilePath = \"<ssh file path>\"\n        passphrase = \"passphrase\"\n    }\n}"))
    }

    if(!credentialProvider.username.isNullOrEmpty()) {
        setCredentialsProvider(UsernamePasswordCredentialsProvider(credentialProvider.username, credentialProvider.password))
    } else {
        setTransportConfigCallback(SshTransportConfigCallback(credentialProvider.sshFilePath, credentialProvider.passphrase))
    }

    return this
}

private class SshTransportConfigCallback(val sshPrivateKeyFile: String?, val passphrase: String?)  : TransportConfigCallback {
    private val sshSessionFactory = object : JschConfigSessionFactory() {
        override fun configure(hc: OpenSshConfig.Host, session: Session) {
            session.setConfig("StrictHostKeyChecking", "no")
        }

        @Throws(JSchException::class)
        override fun createDefaultJSch(fs: FS): JSch {
            val jSch = super.createDefaultJSch(fs)
            jSch.addIdentity(sshPrivateKeyFile, passphrase)
            return jSch
        }
    }

    override fun configure(transport: Transport) {
        val sshTransport = transport as SshTransport
        sshTransport.sshSessionFactory = sshSessionFactory
    }
}