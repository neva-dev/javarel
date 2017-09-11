package com.neva.javarel.gradle.bundle

import com.neva.javarel.gradle.BundlePlugin
import org.gradle.api.Project
import java.io.Serializable

data class Descriptor(val dependencies: Collection<Dependency>) : Serializable {

    companion object {

        fun from(project: Project): Descriptor {
            val dependencies = project.configurations.getByName(BundlePlugin.CONFIG_LIB)
                    .allDependencies.map { Dependency.from(it) }
                    .sortedBy { it.toString() }

            return Descriptor(dependencies)
        }

    }

}