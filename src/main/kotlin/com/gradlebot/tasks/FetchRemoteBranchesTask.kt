package com.gradlebot.tasks

import com.gradlebot.exception.CredentialsNotFoundException
import com.gradlebot.extensions.authenticate
import com.gradlebot.extensions.initRepository
import com.gradlebot.models.Config
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class FetchRemoteBranchesTask : DefaultTask() {
    @Input
    var config: Config? = null

    @TaskAction
    fun fetchRemoteBranches() {
        config?.git?.credentials?.let { credentialProvider ->
            if(credentialProvider.isPresent()) {
                Git(project.initRepository()).use { git ->
                    git.fetch().setRemoveDeletedRefs(true).authenticate(credentialProvider).call()
                    val branches = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call()
                    println(config?.separator)
                    branches.map {
                        it.name.substringAfter("refs/remotes/${config?.git?.remote}/")
                    }.filter {
                        it != "HEAD"
                    }.forEach {
                        println(it)
                    }
                    println(config?.separator)
                }
            } else {
                throw CredentialsNotFoundException()
            }
        }
    }
}