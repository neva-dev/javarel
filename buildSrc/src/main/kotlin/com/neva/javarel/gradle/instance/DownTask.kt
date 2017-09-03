package com.neva.javarel.gradle.instance

import com.neva.javarel.gradle.DefaultTask
import org.gradle.api.tasks.TaskAction

open class DownTask : DefaultTask() {

    companion object {
        val NAME = "instanceDown"
    }

    @TaskAction
    fun up() {
        logger.info("Turning off local instance(s) (${config.localInstances.size})")

        config.localInstances.onEach { LocalHandler(project, it).down() }
    }

}