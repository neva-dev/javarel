package com.neva.javarel.gradle

import com.neva.javarel.gradle.bundle.ConfigureTask
import com.neva.javarel.gradle.bundle.DeployTask
import com.neva.javarel.gradle.bundle.UndeployTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.JavaPlugin
import org.dm.gradle.plugins.bundle.BundlePlugin as InheritPlugin

class BundlePlugin : Plugin<Project> {

    companion object {
        val ID = "com.neva.javarel.bundle"

        val CONFIG_BUNDLE = "bundle"

        val CONFIG_EMBED = "embed"

        val DESCRIPTOR_PATH = "META-INF/javarel/bundle.json"

        val MANIFEST_PATH = "META-INF/MANIFEST.MF"

        val MANIFEST_BUNDLE_PREFIX = "Bundle-"
    }

    override fun apply(project: Project) {
        setupDependentPlugins(project)
        setupOwnTasks(project)
        setupConfigurations(project)
    }

    private fun setupDependentPlugins(project: Project) {
        project.plugins.apply(BasePlugin::class.java)
        project.plugins.apply(JavaPlugin::class.java)
        project.plugins.apply(InheritPlugin::class.java)
    }

    private fun setupOwnTasks(project: Project) {
        val jar = project.tasks.getByName(JavaPlugin.JAR_TASK_NAME)

        val configure = project.tasks.create(ConfigureTask.NAME, ConfigureTask::class.java)
        val deploy = project.tasks.create(DeployTask.NAME, DeployTask::class.java)
        val undeploy = project.tasks.create(UndeployTask.NAME, UndeployTask::class.java)

        jar.dependsOn(configure)
    }

    private fun setupConfigurations(project: Project) {


        val baseConfig = project.configurations.getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME)
        val configurer: (Configuration) -> Unit = {
            it.isTransitive = false
            baseConfig.extendsFrom(it)
        }

        project.configurations.create(CONFIG_EMBED, configurer)
        project.configurations.create(CONFIG_BUNDLE, configurer)
    }

}