package com.gradlebot

import com.gradlebot.auth.CredentialProvider
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

@CacheableTask
open class AssembleWithArgsTask @Inject constructor(private val credentialProvider: CredentialProvider) : DefaultTask() {

    @TaskAction
    fun helloWorld() {
        println(credentialProvider.username)

    }

    override fun getGroup(): String? {
        return "Assemble"
    }

    override fun getDescription(): String? {
        return "Pass command line arguments to assemble task of android"
    }

    override fun getDependsOn(): MutableSet<Any> {
        val dependsOn = super.getDependsOn()
        dependsOn.add(project.tasks.getByName("clean"))
        return dependsOn
    }
}