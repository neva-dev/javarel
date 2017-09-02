package com.neva.javarel.gradle

import com.neva.javarel.gradle.instance.Instance
import com.neva.javarel.gradle.instance.LocalInstance
import com.neva.javarel.gradle.instance.RemoteInstance
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import java.util.*

open class JavarelConfig(project: Project) {

    companion object {
        val NAME = "javarel"

        fun of(project: Project): JavarelConfig {
            val result = project.extensions.findByName(NAME)
                    ?: throw JavarelException("${project.displayName.capitalize()} has neither '${BundlePlugin.ID}' nor '${InstancePlugin.ID}' plugins applied.")

            return result as JavarelConfig
        }

        fun defaultBuildName(project: Project): String {
            return if (project == project.rootProject) {
                project.rootProject.name
            } else {
                "${project.rootProject.name}-${project.path.substring(1).replace(":", "-")}"
            }
        }
    }

    @Input
    var defaultInstances = mutableListOf(
            LocalInstance(
                    project.properties["jv.instance.httpUrl"] as String? ?: "http://localhost:6661",
                    project.properties["jv.instance.name"] as String? ?: "local"
            )
    )

    fun localInstance(url: String, name: String) {
        definedInstances.add(LocalInstance(url, name))
    }

    fun remoteInstance(url: String, name: String, env: String) {
        definedInstances.add(RemoteInstance(url, name, env))
    }

    @Input
    var definedInstances: MutableList<Instance> = mutableListOf()

    @get:Internal
    val instances: List<Instance>
        get() = if (definedInstances.isNotEmpty()) definedInstances else defaultInstances

    @get:Internal
    val localInstances: List<LocalInstance>
        get() = instances.filterIsInstance(LocalInstance::class.java)

    @get:Internal
    val remoteInstances: List<RemoteInstance>
        get() = instances.filterIsInstance(RemoteInstance::class.java)

    @Input
    var distributionUrl = ""

    @Input
    var fileProperties: MutableMap<String, Any> = mutableMapOf()

    @Input
    var fileExpandable: MutableList<String> = mutableListOf(
            "**/*.conf", "**/*.xml", "**/*.properties", "**/*.sh", "**/*.bat", "**/*.ini"
    )

    @Internal
    var buildDate: Date = Date()

    @Input
    var buildName: String = defaultBuildName(project)

}