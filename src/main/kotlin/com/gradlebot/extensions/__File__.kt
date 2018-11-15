package com.gradlebot.extensions

import java.io.File

fun File.deleteDirectory() {
    if(this.exists()) {
        if(this.isDirectory) {
            this.listFiles().forEach {
                it.deleteDirectory()
            }
            this.delete()
        } else {
            this.delete()
        }
    }
}