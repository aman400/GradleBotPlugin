package com.gradlebot.extensions

import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.*

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

fun Project.getProjectProperties(): Properties {
    val propertiesFile = "${project.rootDir.toPath()}${File.separator}gradlebot.properties"
    val gradlebotProperties = Properties()
    try {
        gradlebotProperties.load(FileInputStream(propertiesFile))
    } catch (fileNotFoundException: FileNotFoundException) {
        logger.warn("gradlebot.properties file not found in root project", fileNotFoundException)
    }
    gradlebotProperties.putAll(project.properties)

    return gradlebotProperties
}