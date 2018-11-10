package com.gradlebot.tasks

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.CoreProductFlavor
import com.gradlebot.extensions.isAndroidProject
import com.gradlebot.models.Config
import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class ProductFlavoursTask: DefaultTask() {
    private var productFlavours: NamedDomainObjectContainer<CoreProductFlavor>? = null
    @Input
    var config: Config? = null

    init {
        val hasSubProjects = project.subprojects.size > 0

        if (hasSubProjects) {
            project.subprojects.forEach { subProject ->
                subProject.afterEvaluate {
                    if (it.isAndroidProject()) {
                        productFlavours = it.project.extensions.findByType(BaseExtension::class.java)?.productFlavors
                    }
                }
            }
        }
    }

    @TaskAction
    fun flavours() {
        println("Product Flavours")
        println(config?.separator)
        productFlavours?.forEach {
            println(it.name)
        }
        println(config?.separator)
    }

    @Input
    override fun getGroup(): String? {
        return "Config"
    }

    @Input
    override fun getDescription(): String? {
        return "Get all the Android project build flavours"
    }
}