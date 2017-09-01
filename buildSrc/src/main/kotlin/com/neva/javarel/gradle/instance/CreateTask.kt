package com.neva.javarel.gradle.instance

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class CreateTask : DefaultTask() {

    companion object {
        val NAME = "instanceCreate"
    }

    @TaskAction
    fun create() {
        logger.info("Creating instance")
    }

}