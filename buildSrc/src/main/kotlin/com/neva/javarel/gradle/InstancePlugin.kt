package com.neva.javarel.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class InstancePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.plugins.apply(BasePlugin::class.java)
    }

}