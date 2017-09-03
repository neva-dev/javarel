package com.neva.javarel.gradle.bundle

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import java.io.File

class JarCollector(val project: Project) {

    val jars: List<File>
        get() {
            val config = project.configurations.findByName(Dependency.ARCHIVES_CONFIGURATION)
            if (config != null) {
                return config.allArtifacts.files.files.filter { it.name.endsWith(".jar") }
            }

            return listOf()
        }

    fun config(configName: String): List<File> {
        val config = project.configurations.findByName(configName)
        if (config != null) {
            return config.resolve().toList().filter { it.name.endsWith(".jar") }
        }

        return listOf()
    }

}
