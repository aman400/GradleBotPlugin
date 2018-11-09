package com.gradlebot.extensions

import java.io.File

fun File.deleteDirectory() {
    if(this.exists()) {
        if(this.isDirectory) {
            this.listFiles().forEach {
                it.delete()
            }
        } else {
            this.delete()
        }
    }
}