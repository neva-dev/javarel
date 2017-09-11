package com.neva.javarel.gradle.instance

import com.neva.javarel.gradle.DefaultTask
import org.gradle.api.tasks.TaskAction

open class SetupTask : DefaultTask() {

    companion object {
        val NAME = "instanceSetup"
    }

    @TaskAction
    fun setup() {
        logger.info("Setup of local instance(s) (${config.localInstances.size}) completed successfully.")
    }

}