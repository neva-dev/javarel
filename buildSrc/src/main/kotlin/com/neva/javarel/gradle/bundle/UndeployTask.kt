package com.neva.javarel.gradle.bundle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class UndeployTask : DefaultTask() {

    companion object {
        val NAME = "bundleUndeploy"
    }

    @TaskAction
    fun create() {
        logger.info("Undeploying bundle from instance(s)")
    }

}