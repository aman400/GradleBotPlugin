package com.gradlebot.tasks

import com.gradlebot.extensions.checkout
import com.gradlebot.extensions.initRepository
import com.gradlebot.models.GitConfig
import org.eclipse.jgit.api.Git
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class ResetBranchTask : DefaultTask() {
    @Input
    var config: GitConfig? = null

    @TaskAction
    fun resetBranch() {
        Git(project.initRepository()).use { git ->
            config?.defaultBranch?.let {
                try {
                    git.checkout(it, "${config?.remote}/${config?.branch}")
                    git.branchList().call().forEach { ref ->
                        println(ref.name)
                    }
                    git.branchDelete().setBranchNames(config?.branch).setForce(true).call()
                    logger.debug("deleted branch ${config?.branch}")
                } catch (exception: Exception) {
                    logger.error("unable to delete branch")
                    exception.printStackTrace()
                }
            }
        }
    }
}