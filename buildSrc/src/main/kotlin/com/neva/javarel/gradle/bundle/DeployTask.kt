package com.neva.javarel.gradle.bundle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class DeployTask : DefaultTask() {

    companion object {
        val NAME = "bundleDeploy"
    }

    @TaskAction
    fun create() {
        logger.info("Deploying bundle on instance(s)")
    }

}