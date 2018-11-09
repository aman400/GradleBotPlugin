package com.gradlebot.extensions

import org.gradle.api.Project

fun Project.isAndroidProject(): Boolean {
    return this.plugins.hasPlugin("com.android.application")
}