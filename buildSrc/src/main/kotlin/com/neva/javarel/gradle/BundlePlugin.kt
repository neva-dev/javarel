package com.neva.javarel.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.osgi.OsgiPlugin
import org.gradle.api.tasks.bundling.Jar

class BundlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.plugins.apply(JavaPlugin::class.java)
        project.plugins.apply(OsgiPlugin::class.java)

        val jar = project.tasks.getByName(JavaPlugin.JAR_TASK_NAME) as Jar
        jar.archiveName = "${project.rootProject.name}-${project.name}-${project.version}.jar"
    }

}