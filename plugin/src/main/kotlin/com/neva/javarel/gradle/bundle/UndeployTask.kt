package com.neva.javarel.gradle.bundle

import com.neva.javarel.gradle.DefaultTask
import org.gradle.api.tasks.TaskAction

open class UndeployTask : DefaultTask() {

    companion object {
        val NAME = "bundleUndeploy"
    }

    @TaskAction
    fun undeploy() {
        logger.info("Undeploying bundle from instance(s) (${config.instances.size})")
    }

}