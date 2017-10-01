package com.neva.javarel.gradle.bundle

import com.neva.javarel.gradle.BundlePlugin
import com.neva.javarel.gradle.internal.Formats
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency.ARCHIVES_CONFIGURATION
import org.zeroturnaround.zip.ZipUtil
import java.io.File

class JarCollector(val project: Project) {

    val artifacts: List<File>
        get() {
            val config = project.configurations.findByName(ARCHIVES_CONFIGURATION)
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

    val libs: Collection<File>
        get() = dependencies(BundlePlugin.CONFIG_LIB)

    // TODO why commons IO are not included here (newest version is grabbed)
    // split all deps into separate configurations to resolve all files as in:
    // https://github.com/renatoathaydes/osgi-run/blob/master/osgi-run-core/src/main/groovy/com/athaydes/gradle/osgi/ConfigurationsCreator.groovy
    val apps: Collection<File>
        get() {
            val jars = dependencies(BundlePlugin.CONFIG_APP)
            val appDeps = project.configurations.getByName(BundlePlugin.CONFIG_APP)
                    .allDependencies.map { Dependency.from(it) }
            val appJars = jars.filter { jar ->
                appDeps.any { it.matches(jar) }
            }
            val bundleDeps = appJars.flatMap { jar ->
                val bytes: ByteArray? = ZipUtil.unpackEntry(jar, BundlePlugin.DESCRIPTOR_PATH, Charsets.UTF_8)

                if (bytes == null) {
                    listOf()
                } else {
                    val json = String(bytes, Charsets.UTF_8)
                    val descriptor = Formats.fromJson(json, Descriptor::class.java)

                    descriptor.dependencies
                }
            }
            val depJars = (jars - appJars).filter { jar ->
                bundleDeps.any { it.matches(jar) }
            }

            return appJars + depJars
        }

    val all: Collection<File>
        get() {
            val jars = mutableSetOf<File>()

            jars += artifacts
            jars += libs
            jars += apps

            return jars
        }
}
