package com.gradlebot.jgit

import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import org.eclipse.jgit.api.TransportConfigCallback
import org.eclipse.jgit.transport.*
import org.eclipse.jgit.util.FS

class SshTransportConfigCallback(privateSSHFilepath: String?, sshPassphrase: String?) : TransportConfigCallback {
    private val sshSessionFactory = object : JschConfigSessionFactory() {
        override fun configure(hc: OpenSshConfig.Host, session: Session) {
            session.setConfig("StrictHostKeyChecking", "no")
        }

        override fun createDefaultJSch(fs: FS?): JSch {
            val defaultJsch = super.createDefaultJSch(fs)
            if(privateSSHFilepath != null) {
                if(sshPassphrase != null) {
                    defaultJsch.addIdentity(privateSSHFilepath, sshPassphrase)
                } else {
                    defaultJsch.addIdentity(privateSSHFilepath)
                }
            }
            return defaultJsch

        }
    }

    override fun configure(transport: Transport) {
        val sshTransport = transport as SshTransport
        sshTransport.sshSessionFactory = sshSessionFactory
    }
}