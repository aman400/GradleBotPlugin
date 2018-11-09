package com.gradlebot.tasks

import com.gradlebot.auth.CredentialProvider
import com.gradlebot.extensions.authenticate
import com.gradlebot.models.Config
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

open class PullCodeTask : DefaultTask() {
    var credentialProvider: CredentialProvider = project.objects.newInstance(CredentialProvider::class.java)

    var config: Config? = null
    private lateinit var repository: Repository

    fun credentials(action: Action<CredentialProvider>) {
        action.execute(credentialProvider)
    }

    @TaskAction
    fun pullCode() {
        config?.branch?.let { branchName ->
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
                if (git.pull().authenticate(credentialProvider).call().isSuccessful) {
                    logger.quiet("Pulled latest code")
                }
            } else {
                val remoteBranch = findRemoteBranch(git, branchName)
                if (remoteBranch != null) {
                    fetchAndCheckoutRemoteBranch(git, branchName)
                } else {
                    git.fetch().authenticate(credentialProvider).call()
                    logger.quiet("Fetched all branches")
                    fetchAndCheckoutRemoteBranch(git, branchName)
                }
            }
        } ?: println("BRANCH not specified")
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
        if (git.pull().setRemoteBranchName(branchName).authenticate(credentialProvider).call().isSuccessful) {
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