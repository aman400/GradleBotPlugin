package com.gradlebot.tasks

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.ProductFlavor
import com.gradlebot.models.Config
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

open class ProductFlavoursTask: BaseAndroidTask() {
    @Internal
    var productFlavours: NamedDomainObjectContainer<ProductFlavor>? = null
    @Input
    var config: Config? = null

    @TaskAction
    fun flavours() {
        logger.info("Product Flavours")
        println(config?.separator)
        productFlavours?.forEach {
            println(it.name)
        }
        println(config?.separator)
    }

    @Input
    override fun getGroup(): String {
        return "Config"
    }

    @Input
    override fun getDescription(): String {
        return "Get all the Android project build flavours"
    }

    override fun evaluateTask() {
        productFlavours = androidProject?.extensions?.findByType(BaseExtension::class.java)?.productFlavors
    }
}