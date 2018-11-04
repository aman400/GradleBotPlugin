package com.gradlebot

import com.gradlebot.auth.CredentialProvider
import org.eclipse.jgit.api.Git
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
        val listBranchCommand = git.branchList()
        val branch = listBranchCommand.call().firstOrNull {
            it.name == branchName
        }
        println(branchName)
        if(branch != null) {
            git.checkout().setName(branch.name).setCreateBranch(false).call()
            if(git.pull().call().isSuccessful) {
                logger.quiet("Pulled latest code")
            }
        } else {
            val fetchResult = git.fetch().call()
            logger.quiet("Fetched all branches")
        }
    }

    override fun getGroup(): String? {
        return "VCS"
    }

    override fun getDescription(): String? {
        return "Pull latest code from github"
    }
}