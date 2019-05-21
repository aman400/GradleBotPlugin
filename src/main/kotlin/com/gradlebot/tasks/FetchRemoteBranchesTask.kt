package com.gradlebot.tasks

import com.gradlebot.exception.CredentialsNotFoundException
import com.gradlebot.extensions.authenticate
import com.gradlebot.extensions.initRepository
import com.gradlebot.models.Config
import com.gradlebot.models.UserConfig
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.transport.TagOpt
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class FetchRemoteBranchesTask : DefaultTask() {
    @Input
    var userConfig: UserConfig? = null
    @Input
    var config: Config? = null

    @TaskAction
    fun fetchRemoteBranches() {
        userConfig?.git?.credentials?.let { credentialProvider ->
            if (credentialProvider.isPresent()) {
                Git(project.initRepository()).use { git ->
                    // Fetch all remote branches
                    git.fetch().setRemote(userConfig?.git?.remote)
                        .setRemoveDeletedRefs(true)
                        .setTagOpt(TagOpt.FETCH_TAGS)
                        .authenticate(credentialProvider).call()
                    val branches = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call()
                    println(config?.separator)

                    // Filter branches of selected remote
                    branches.filter { ref ->
                        userConfig?.git?.remote?.let { remote ->
                            // Remove default HEAD
                            ref.name.contains(remote) && !ref.name.contains("HEAD", ignoreCase = true)
                        } ?: true
                    }.map {
                        // Strip off remote from branch name
                        it.name.substringAfter("refs/remotes/${userConfig?.git?.remote ?: "origin"}/")
                    }.forEach {
                        println(it)
                    }
                    val tags = git.tagList().call()
                    RevWalk(git.repository).use { revWalk ->
                        tags.sortByDescending {
                            revWalk.parseCommit(it.objectId).commitTime
                        }
                    }
                    tags.map {
                        it.name.substringAfter("refs/tags/")
                    }.forEach {
                        println("#tag-$it")
                    }
                    println(config?.separator)
                }
            } else {
                throw CredentialsNotFoundException(credentialProvider.getErrorMessage())
            }
        }
    }
}