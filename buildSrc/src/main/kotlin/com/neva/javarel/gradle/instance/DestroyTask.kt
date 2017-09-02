package com.neva.javarel.gradle.instance

import com.neva.javarel.gradle.DefaultTask
import org.gradle.api.tasks.TaskAction

open class DestroyTask : DefaultTask() {

    companion object {
        val NAME = "instanceDestroy"
    }

    @TaskAction
    fun destroy() {
        logger.info("Destroying local instance(s) (${config.localInstances.size})")

        config.localInstances.onEach { LocalHandler(project, it).destroy() }
    }

}