package com.neva.javarel.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.osgi.OsgiPlugin

class BundlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.plugins.apply(JavaPlugin::class.java)
        project.plugins.apply(OsgiPlugin::class.java)
    }

}