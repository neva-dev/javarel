package com.neva.javarel.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

import org.gradle.api.plugins.BasePlugin as GradleBasePlugin

class BasePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.plugins.apply(GradleBasePlugin::class.java)
    }

}