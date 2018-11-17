package com.gradlebot.extensions

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.api.Project
import java.io.File

fun Project.isAndroidProject(): Boolean {
    return this.plugins.hasPlugin("com.android.application")
}

fun Project.isAndroidLibrary(): Boolean {
    return this.plugins.hasPlugin("com.android.library")
}

fun Project.initGit(): Git {
    val repositoryBuilder = FileRepositoryBuilder()
    repositoryBuilder.isMustExist = true
    repositoryBuilder.findGitDir()
    repositoryBuilder.readEnvironment()
    repositoryBuilder.gitDir = File("${project.rootDir}${File.separator}.git")
    val repository = repositoryBuilder.build()
    return Git(repository)
}