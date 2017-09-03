package com.neva.javarel.gradle.instance

import com.neva.javarel.gradle.BundlePlugin
import com.neva.javarel.gradle.DefaultTask
import com.neva.javarel.gradle.bundle.JarCollector
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import java.io.File

open class SetupTask : DefaultTask() {

    companion object {
        val NAME = "instanceSetup"
    }

    init {
        dependsOn(bundleProjects)
    }

    val bundleProjects: List<Project>
        get() = project.allprojects.filter { it == project || it.path.startsWith(project.path) }.filter { it.plugins.findPlugin(BundlePlugin.ID) != null }

    private val bundles: List<File>
        get() = bundleProjects.flatMap {
            val collector = JarCollector(it)

            collector.jars + collector.config(BundlePlugin.CONFIG_BUNDLE)
        }

    @TaskAction
    fun setup() {
        logger.info("Setup of local instance(s) (${config.localInstances.size}) completed successfully.")

        config.localInstances.forEach { LocalHandler(project, it).setup(bundles) }
    }

}