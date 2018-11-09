package com.gradlebot.tasks

import com.gradlebot.auth.CredentialProvider
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction

@CacheableTask
open class AssembleWithArgsTask: DefaultTask() {
    lateinit var credentialProvider: CredentialProvider

    @TaskAction
    fun helloWorld() {
        println(credentialProvider.passphrase)
    }

    override fun getGroup(): String? {
        return "Assemble"
    }

    override fun getDescription(): String? {
        return "Pass command line arguments to assemble task of android"
    }
}