package com.neva.javarel.gradle.instance

import com.neva.javarel.gradle.BundlePlugin
import com.neva.javarel.gradle.DefaultTask
import com.neva.javarel.gradle.bundle.JarCollector
import org.gradle.api.Project
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin
import java.io.File

open class SetupTask : DefaultTask() {

    companion object {
        val NAME = "instanceSetup"
    }

    init {
        project.gradle.projectsEvaluated({
            dependsOn(bundleProjects.map {
                it.tasks.getByName(LifecycleBasePlugin.BUILD_TASK_NAME)
            })
        })
    }

    @get:Internal
    private val bundleProjects: List<Project>
        get() = project.allprojects.filter {
            (it == project || it.path.startsWith(project.path))
                    && it.plugins.findPlugin(BundlePlugin.ID) != null
        }

    @get:Internal
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