package com.neva.javarel.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

import org.gradle.api.plugins.BasePlugin as GradleBasePlugin

class BasePlugin : Plugin<Project> {

    companion object {
        val PKG = "com.neva.javarel.gradle"

        val ID = "com.neva.javarel.base"
    }

    override fun apply(project: Project) {
        project.plugins.apply(GradleBasePlugin::class.java)
        project.extensions.create(JavarelConfig.NAME, JavarelConfig::class.java, project)
    }

}