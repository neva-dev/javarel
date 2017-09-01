package com.neva.javarel.gradle

import com.neva.javarel.gradle.instance.Instance
import org.gradle.api.Project

open class JavarelConfig(project: Project) {

    companion object {
        val NAME = "javarel"

        fun of(project: Project): JavarelConfig {
            val result = project.extensions.findByName(NAME)
                    ?: throw JavarelException("${project.displayName.capitalize()} has neither '${BundlePlugin.ID}' nor '${InstancePlugin.ID}' plugins applied.")

            return result as JavarelConfig
        }
    }

    var instances: MutableList<Instance> = mutableListOf(
            Instance(project.properties["javarel.instance.url"] as String? ?: "http://localhost:6661")
    )

    fun localInstance(url: String) {
        instances.add(Instance(url))
    }

}