package com.neva.javarel.gradle.bundle

import com.neva.javarel.gradle.DefaultTask
import org.gradle.api.tasks.TaskAction

open class DeployTask : DefaultTask() {

    companion object {
        val NAME = "bundleDeploy"
    }

    @TaskAction
    fun deploy() {
        logger.info("Deploying bundle on instance(s) (${config.instances.size})")


        // JarCollector(project).all
    }

}