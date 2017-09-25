package com.neva.javarel.gradle.bundle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.neva.javarel.gradle.BundlePlugin
import org.gradle.api.Project
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
class Descriptor private constructor() : Serializable {

    companion object {

        fun from(project: Project): Descriptor {
            val result = Descriptor()
            result.dependencies = project.configurations.getByName(BundlePlugin.CONFIG_LIB)
                    .allDependencies.map { Dependency.from(it) }
                    .sortedBy { it.gradlePath }

            return result
        }

    }

    lateinit var dependencies: Collection<Dependency>

}