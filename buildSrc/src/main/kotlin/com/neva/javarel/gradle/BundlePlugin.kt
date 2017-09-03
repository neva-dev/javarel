package com.neva.javarel.gradle

import com.neva.javarel.gradle.bundle.DeployTask
import com.neva.javarel.gradle.bundle.UndeployTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.osgi.OsgiPlugin
import org.gradle.api.tasks.bundling.Jar

class BundlePlugin : Plugin<Project> {

    companion object {
        val ID = "com.neva.javarel.bundle"

        val CONFIG_BUNDLE = "bundle"

        val CONFIG_INSTALL = "install"

        val CONFIG_EMBED = "embed"
    }

    override fun apply(project: Project) {
        setupDependentPlugins(project)
        setupConfigurations(project)
        setupOwnTasks(project)
        setupOtherTasks(project)
    }

    private fun setupOtherTasks(project: Project) {
        val jar = project.tasks.getByName(JavaPlugin.JAR_TASK_NAME) as Jar
        jar.archiveName = "${project.rootProject.name}-${project.name}-${project.version}.jar"
    }

    private fun setupDependentPlugins(project: Project) {
        project.plugins.apply(JavaPlugin::class.java)
        project.plugins.apply(OsgiPlugin::class.java) // bundle plugin to parse new osgi annotations
    }

    private fun setupOwnTasks(project: Project) {
        project.tasks.create(DeployTask.NAME, DeployTask::class.java)
        project.tasks.create(UndeployTask.NAME, UndeployTask::class.java)
    }

    private fun setupConfigurations(project: Project) {
        project.plugins.withType(JavaPlugin::class.java, {
            val baseConfig = project.configurations.getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME)
            val configurer: (Configuration) -> Unit = {
                it.isTransitive = false
                baseConfig.extendsFrom(it)
            }

            project.configurations.create(CONFIG_EMBED, configurer)
            project.configurations.create(CONFIG_BUNDLE, configurer)
        })
    }

}