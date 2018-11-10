package com.gradlebot.tasks

import com.gradlebot.models.Config
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

open class FetchRemoteBranchesTask : DefaultTask() {
    @Input
    var config: Config? = null
    private lateinit var repository: Repository

    @TaskAction
    fun fetchRemoteBranches() {
        val repositoryBuilder = FileRepositoryBuilder()
        repositoryBuilder.isMustExist = true
        repositoryBuilder.gitDir = File("${project.projectDir}${File.separator}.git")
        repository = repositoryBuilder.build()
        val git = Git(repository)
        val branches = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call()
        println(config?.separator)
        branches.forEach {
            println(it.name)
        }
        println(config?.separator)
    }
}