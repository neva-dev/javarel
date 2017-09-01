package com.neva.javarel.gradle

import com.neva.javarel.gradle.bundle.DeployTask
import com.neva.javarel.gradle.bundle.UndeployTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.osgi.OsgiPlugin
import org.gradle.api.tasks.bundling.Jar

class BundlePlugin : Plugin<Project> {

    companion object {
        val ID = "com.neva.javarel.bundle"
    }

    override fun apply(project: Project) {
        project.plugins.apply(JavaPlugin::class.java)
        project.plugins.apply(OsgiPlugin::class.java) // bundle plugin to parse new osgi annotations

        val jar = project.tasks.getByName(JavaPlugin.JAR_TASK_NAME) as Jar
        jar.archiveName = "${project.rootProject.name}-${project.name}-${project.version}.jar"

        project.tasks.create(DeployTask.NAME, DeployTask::class.java)
        project.tasks.create(UndeployTask.NAME, UndeployTask::class.java)
    }

}