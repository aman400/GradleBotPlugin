package com.gradlebot.models

open class Config {
    var buildType: String? = null
    var branch: String? = null
    var flavour: String? = null
    var destinationPath: String? = null
    var filePrefix: String? = null
    var separator: String = "<========>"
    var origin: String = "origin"
}