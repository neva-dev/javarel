package com.neva.javarel.gradle.bundle

import com.neva.javarel.gradle.BundlePlugin
import com.neva.javarel.gradle.DefaultTask
import com.neva.javarel.gradle.JavarelTask
import com.neva.javarel.gradle.internal.Formats
import org.dm.gradle.plugins.bundle.BundleExtension
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.osgi.OsgiManifest
import org.gradle.api.tasks.*
import org.gradle.jvm.tasks.Jar
import java.io.File

/**
 * Update manifest being used by 'jar' task of Java Plugin.
 *
 * Both plugins 'osgi' and 'org.dm.bundle' are supported.
 *
 * @see <https://issues.gradle.org/browse/GRADLE-1107>
 * @see <https://github.com/TomDmitriev/gradle-bundle-plugin>
 */
open class ConfigureTask : DefaultTask() {

    companion object {
        val NAME = "bundleConfigure"

        val OSGI_PLUGIN_ID = "osgi"

        val BUNDLE_PLUGIN_ID = "org.dm.bundle"
    }

    init {
        description = "Configure JAR to be a valid Javarel bundle."
    }

    @Internal
    val jar = project.tasks.getByName(JavaPlugin.JAR_TASK_NAME) as Jar

    @get:InputFiles
    val embeddableJars: List<File>
        get() {
            return project.configurations.getByName(BundlePlugin.CONFIG_EMBED).files.sortedBy { it.name }
        }

    @get:Input
    val bundleDescriptor
        get() = Descriptor.from(project)

    @get:OutputFile
    val bundleDescriptorFile = JavarelTask.temporaryFile(project, NAME, BundlePlugin.DESCRIPTOR_NAME)

    init {
        applyDefaults()

        project.afterEvaluate {
            embedJars()
        }
    }

    private fun applyDefaults() {
        jar.archiveName = "${project.rootProject.name}-${project.name}-${project.version}.jar"
        jar.from(bundleDescriptorFile) { spec ->
            spec.into(BundlePlugin.DESCRIPTOR_DIR) // TODO not working
        }
    }

    private fun embedJars() {
        if (embeddableJars.isEmpty()) {
            return
        }

        project.logger.info("Embedding JAR files: ${embeddableJars.map { it.name }}")

        jar.from(embeddableJars)
        addInstruction("Bundle-ClassPath", {
            val list = mutableListOf(".")
            embeddableJars.onEach { jar -> list.add(jar.name) }
            list.joinToString(",")
        })
    }

    private fun addInstruction(name: String, valueProvider: () -> String) {
        if (project.plugins.hasPlugin(OSGI_PLUGIN_ID)) {
            addInstruction(jar.manifest as OsgiManifest, name, valueProvider())
        } else if (project.plugins.hasPlugin(BUNDLE_PLUGIN_ID)) {
            addInstruction(project.extensions.getByType(BundleExtension::class.java), name, valueProvider())
        } else {
            project.logger.warn("Cannot apply specific OSGi instruction to JAR manifest, because neither "
                    + "'$OSGI_PLUGIN_ID' nor '$BUNDLE_PLUGIN_ID' are applied to project '${project.name}'.")
        }
    }

    private fun addInstruction(manifest: OsgiManifest, name: String, value: String) {
        if (!manifest.instructions.containsKey(name)) {
            if (value.isNotBlank()) {
                manifest.instruction(name, value)
            }
        }
    }

    @Suppress("unchecked_cast")
    private fun addInstruction(config: BundleExtension, name: String, value: String) {
        val instructions = config.instructions as Map<String, Any>
        if (!instructions.contains(name)) {
            if (value.isNotBlank()) {
                config.instruction(name, value)
            }
        }
    }

    @TaskAction
    fun configure() {
        generateDescriptor()
    }

    private fun generateDescriptor() {
        val json = Formats.toJson(bundleDescriptor)

        bundleDescriptorFile.printWriter().use { it.print(json) }
    }

}