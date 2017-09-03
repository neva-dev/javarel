package com.neva.javarel.gradle.instance

import com.neva.javarel.gradle.DefaultTask
import org.gradle.api.tasks.TaskAction

open class UpTask : DefaultTask() {

    companion object {
        val NAME = "instanceUp"
    }

    @TaskAction
    fun up() {
        logger.info("Turning on local instance(s) (${config.localInstances.size})")

        config.localInstances.onEach { LocalHandler(project, it).up() }
    }

}