package com.neva.javarel.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

import org.gradle.api.plugins.BasePlugin as GradleBasePlugin

class BasePlugin : Plugin<Project> {

    companion object {
        val NAME = "Javarel Gradle Plugin"

        val PKG = "com.neva.javarel.gradle"

        val ID = "com.neva.javarel.base"

        val VERSION by lazy {
            BasePlugin::class.java.`package`.implementationVersion
        }

    }

    override fun apply(project: Project) {
        project.plugins.apply(GradleBasePlugin::class.java)
        project.extensions.create(JavarelConfig.NAME, JavarelConfig::class.java, project)
    }

}