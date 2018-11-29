package com.gradlebot.jgit

import org.gradle.internal.impldep.com.jcraft.jsch.JSch
import org.gradle.internal.impldep.com.jcraft.jsch.JSchException
import org.gradle.internal.impldep.com.jcraft.jsch.Session
import org.gradle.internal.impldep.org.eclipse.jgit.api.TransportConfigCallback
import org.gradle.internal.impldep.org.eclipse.jgit.transport.JschConfigSessionFactory
import org.gradle.internal.impldep.org.eclipse.jgit.transport.OpenSshConfig
import org.gradle.internal.impldep.org.eclipse.jgit.transport.SshTransport
import org.gradle.internal.impldep.org.eclipse.jgit.transport.Transport
import org.gradle.internal.impldep.org.eclipse.jgit.util.FS


class SshTransportConfigCallback(val sshPrivateKeyFile: String?, val passphrase: String?)  : TransportConfigCallback {
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