package com.gradlebot.extensions

import org.gradle.api.Project
import org.gradle.internal.impldep.org.eclipse.jgit.lib.Repository
import org.gradle.internal.impldep.org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.File

fun Project.isAndroidProject(): Boolean {
    return this.plugins.hasPlugin("com.android.application")
}

fun Project.isAndroidLibrary(): Boolean {
    return this.plugins.hasPlugin("com.android.library")
}

fun Project.initRepository(): Repository? {
    val repositoryBuilder = FileRepositoryBuilder()
    repositoryBuilder.isMustExist = true
    repositoryBuilder.findGitDir()
    repositoryBuilder.readEnvironment()
    repositoryBuilder.gitDir = File("${project.rootDir}${File.separator}.git")
    return repositoryBuilder.build()
}