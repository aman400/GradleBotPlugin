package com.gradlebot

import com.gradlebot.auth.CredentialProvider
import com.gradlebot.extensions.authanticate
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

open class PullCodeTask @Inject constructor(val credentialProvider: CredentialProvider) : DefaultTask() {

    private lateinit var repository: Repository

    @TaskAction
    fun pullCode() {
        val branchName: String = project.property("BRANCH").toString()
        val repositoryBuilder = FileRepositoryBuilder()
        repositoryBuilder.isMustExist = true
        repositoryBuilder.gitDir = File("${project.projectDir}${File.separator}.git")
        repository = repositoryBuilder.build()
        val git = Git(repository)

        val localBranch = git.branchList().call().firstOrNull {
            it.name.substringAfter("refs/heads/") == branchName
        }

        if (localBranch != null) {
            git.checkout().setName(branchName).setCreateBranch(false).call()
            if (git.pull().authanticate(credentialProvider).call().isSuccessful) {
                logger.quiet("Pulled latest code")
            }
        } else {
            val remoteBranch = findRemoteBranch(git, branchName)
            if (remoteBranch != null) {
                fetchAndCheckoutRemoteBranch(git, branchName)
            } else {
                git.fetch().authanticate(credentialProvider).call()
                logger.quiet("Fetched all branches")
                fetchAndCheckoutRemoteBranch(git, branchName)
            }
        }
    }

    private fun fetchAndCheckoutRemoteBranch(git: Git, branchName: String) {
        checkoutRemoteBranch(git, branchName)

        if (pullRemoteCode(git, branchName)) {
            logger.quiet("Pulled latest code from remote branch")
        } else {
            logger.quiet("Unable to pull latest code from branch")
        }
    }

    private fun findRemoteBranch(git: Git, branchName: String): Ref? {
        return git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call().firstOrNull {
            it.name.substringAfter("refs/remotes/origin/") == branchName
        }
    }

    private fun checkoutRemoteBranch(git: Git, branchName: String) {
        git.checkout()
            .setName(branchName)
            .setCreateBranch(true)
            .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
            .setStartPoint("origin/$branchName")
            .call()
    }

    private fun pullRemoteCode(git: Git, branchName: String): Boolean {
        if (git.pull().setRemoteBranchName(branchName).authanticate(credentialProvider).call().isSuccessful) {
            return true
        }
        return false
    }

    override fun getGroup(): String? {
        return "VCS"
    }

    override fun getDescription(): String? {
        return "Pull latest code from github"
    }
}