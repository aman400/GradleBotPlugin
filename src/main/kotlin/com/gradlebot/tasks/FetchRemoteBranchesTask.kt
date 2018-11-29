package com.gradlebot.tasks

import com.gradlebot.auth.CredentialProvider
import com.gradlebot.extensions.authenticate
import com.gradlebot.extensions.initRepository
import com.gradlebot.models.Config
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.impldep.org.eclipse.jgit.api.Git
import org.gradle.internal.impldep.org.eclipse.jgit.api.ListBranchCommand

open class FetchRemoteBranchesTask : DefaultTask() {
    @Input
    var config: Config? = null

    @Input
    var credentials: CredentialProvider? = null

    @TaskAction
    fun fetchRemoteBranches() {
        credentials?.let {credentialProvider ->
            Git(project.initRepository()).use { git ->
                git.fetch().setRemoveDeletedRefs(true).authenticate(credentialProvider).call()
                val branches = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call()
                println(config?.separator)
                branches.map {
                    it.name.substringAfter("refs/remotes/${config?.remote}/")
                }.filter {
                    it != "HEAD"
                }.forEach {
                    println(it)
                }
                println(config?.separator)
            }
        }
    }
}