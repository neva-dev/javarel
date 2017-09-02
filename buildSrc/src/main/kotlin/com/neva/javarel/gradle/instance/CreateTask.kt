package com.neva.javarel.gradle.instance

import com.neva.javarel.gradle.DefaultTask
import org.gradle.api.tasks.TaskAction

open class CreateTask : DefaultTask() {

    companion object {
        val NAME = "instanceCreate"
    }

    @TaskAction
    fun create() {
        logger.info("Creating local instance(s) (${config.localInstances.size})")

        config.localInstances.onEach { LocalHandler(project, it).create() }
    }

}