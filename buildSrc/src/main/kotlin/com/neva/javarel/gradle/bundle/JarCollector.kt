package com.neva.javarel.gradle.bundle

import com.neva.javarel.gradle.BundlePlugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import java.io.File

class JarCollector(val project: Project) {

    val artifacts: List<File>
        get() {
            val config = project.configurations.findByName(Dependency.ARCHIVES_CONFIGURATION)
            if (config != null) {
                return config.allArtifacts.files.files.filter { it.name.endsWith(".jar") }
            }

            return listOf()
        }

    fun dependencies(configName: String): List<File> {
        val config = project.configurations.findByName(configName)
        if (config != null) {
            return config.resolve().toList().filter { it.name.endsWith(".jar") }
        }

        return listOf()
    }

    val all: Collection<File>
        get() {
            val jars = mutableSetOf<File>()

            jars += artifacts
            jars += dependencies(BundlePlugin.CONFIG_BUNDLE)

            return jars
        }
}
