package com.gradlebot.tasks

import com.gradlebot.auth.CredentialProvider
import com.gradlebot.extensions.authenticate
import com.gradlebot.extensions.initRepository
import com.gradlebot.models.GitConfig
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.transport.TagOpt
import org.gradle.api.DefaultTask
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class PullCodeTask : DefaultTask() {

    @Input
    var gitConfig: GitConfig? = null

    @TaskAction
    fun pullCode() {
        gitConfig?.let { config ->
            if (config.credentials.isPresent()) {
                if (!config.branch.isNullOrEmpty()) {
                    Git(project.initRepository()).use { git ->

                        git.fetch().setRemote(gitConfig?.remote).setCheckFetchedObjects(true)
                            .setRemoveDeletedRefs(true)
                            .setTagOpt(TagOpt.FETCH_TAGS)
                            .authenticate(config.credentials).call()
                        logger.quiet("Fetched all branches")

                        val localBranch = git.branchList().call().filter {
                            it.name.substringAfter("refs/heads/") == config.branch
                        }.map {
                            it.name.substringAfter("refs/heads/")
                        }.firstOrNull()

                        if (localBranch != null) {
                            git.checkout().setName(localBranch).call()
                            if (git.pull().setRemoteBranchName(localBranch)
                                    .authenticate(config.credentials).call().isSuccessful) {
                                logger.quiet("Pulled latest code")
                            } else {
                                logger.warn("Unable to pull code")
                            }
                        } else {
                            val localTag = git.tagList().call().firstOrNull { ref ->
                                config.branch?.let {
                                    ref.name.endsWith(it, true)
                                } ?: false
                            }
                            if(localTag != null) {
                                try {
                                    git.checkout().setCreateBranch(false).setName(config.branch)
                                        .setStartPoint(localTag.name).call()
                                } catch (exception: Exception) {
                                    exception.printStackTrace()
                                }
                            } else {
                                val remoteBranch =
                                    git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call().filter {
                                        it.name.substringAfter("refs/remotes/${config.remote}/") == config.branch
                                    }.map {
                                        it.name.substringAfter("refs/remotes/${config.remote}/")
                                    }.firstOrNull()

                                if (remoteBranch != null) {
                                    fetchAndCheckoutRemoteBranch(git, config, config.credentials)
                                } else {
                                    null
                                }
                            }
                        }
                    }
                } else {
                    logger.warn("branch not specified")
                }
            } else {
                logger.warn("Either set username and password or setup ssh with following block\nbot {\n    credentials {\n        username = \"<username>\"\n        password = \"<password>\"\n        sshFilePath = \"<ssh file path>\"\n        passphrase = \"<passphrase>\"\n    }\n}")
            }
        }
    }

    private fun fetchAndCheckoutRemoteBranch(git: Git, gitConfig: GitConfig, credentialProvider: CredentialProvider) {
        checkoutRemoteBranch(git, gitConfig)

        if (pullRemoteCode(git, gitConfig.branch!!, credentialProvider)) {
            logger.quiet("Pulled latest code from remote branch")
        } else {
            logger.quiet("Unable to pull latest code from branch")
        }
    }

    private fun checkoutRemoteBranch(git: Git, config: GitConfig) {
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
    override fun getGroup(): String {
        return "VCS"
    }

    @Input
    override fun getDescription(): String {
        return "Pull latest code from github"
    }
}