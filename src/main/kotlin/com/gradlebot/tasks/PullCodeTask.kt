package com.gradlebot.tasks

import com.gradlebot.auth.CredentialProvider
import com.gradlebot.extensions.authenticate
import com.gradlebot.extensions.initGit
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
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject
import kotlin.math.log

open class PullCodeTask : DefaultTask() {
    @Input
    var credentialProvider: CredentialProvider? = null

    @Input
    var config: Config? = null

    @TaskAction
    fun pullCode() {
        credentialProvider?.let { credentials ->
            if (credentials.isPresent()) {
                if (!config?.branch.isNullOrEmpty()) {
                    config?.let { config ->
                        with(project.initGit()) {
                            val localBranch = branchList().call().firstOrNull {
                                it.name.substringAfter("refs/heads/") == config.branch
                            }

                            if (localBranch != null) {
                                checkout().setName(config.branch).setCreateBranch(false).call()
                                if (pull().authenticate(credentials).call().isSuccessful) {
                                    logger.quiet("Pulled latest code")
                                }
                            } else {
                                val remoteBranch = findRemoteBranch(this, config)
                                if (remoteBranch != null) {
                                    fetchAndCheckoutRemoteBranch(this, config, credentials)
                                } else {
                                    fetch().authenticate(credentials).call()
                                    logger.quiet("Fetched all branches")
                                    fetchAndCheckoutRemoteBranch(this, config, credentials)
                                }
                            }
                            close()
                        }
                    } ?: logger.warn("branch not specified")
                } else {
                    logger.warn("branch not specified")
                }
            } else {
                logger.warn("Either set username and password or setup ssh with following block\nbot {\n    credentials {\n        username = \"<username>\"\n        password = \"<password>\"\n        sshFilePath = \"<ssh file path>\"\n        passphrase = \"<passphrase>\"\n    }\n}")
            }
        }
    }

    private fun fetchAndCheckoutRemoteBranch(git: Git, config: Config, credentialProvider: CredentialProvider) {
        checkoutRemoteBranch(git, config)

        if (pullRemoteCode(git, config.branch!!, credentialProvider)) {
            logger.quiet("Pulled latest code from remote branch")
        } else {
            logger.quiet("Unable to pull latest code from branch")
        }
    }

    private fun findRemoteBranch(git: Git, config: Config): Ref? {
        return git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call().firstOrNull {
            it.name.substringAfter("refs/remotes/${config.remote}/") == config.branch
        }
    }

    private fun checkoutRemoteBranch(git: Git, config: Config) {
        git.checkout()
            .setName(config.branch)
            .setCreateBranch(true)
            .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
            .setStartPoint("${config.remote}/${config.branch}")
            .call()
    }

    private fun pullRemoteCode(git: Git, branchName: String, credentialProvider: CredentialProvider): Boolean {
        if (git.pull().setRemoteBranchName(branchName).authenticate(credentialProvider).call().isSuccessful) {
            return true
        }
        return false
    }

    @Input
    override fun getGroup(): String? {
        return "VCS"
    }

    @Input
    override fun getDescription(): String? {
        return "Pull latest code from github"
    }
}