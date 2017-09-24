package com.neva.javarel.gradle.instance

import com.neva.javarel.gradle.JavarelConfig
import com.neva.javarel.gradle.internal.PropertyParser
import com.neva.javarel.gradle.internal.file.FileOperations
import com.neva.javarel.gradle.internal.file.FileResolver
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.internal.os.OperatingSystem
import org.gradle.util.GFileUtils
import org.zeroturnaround.zip.ZipUtil
import java.io.File

class LocalHandler(val project: Project, val instance: Instance) {

    companion object {
        val DISTRIBUTION_RESOURCE = "instance/distribution/source/org.apache.felix.main.distribution-5.6.8.zip"

        val DISTRIBUTION_OVERRIDES = "instance/distribution/overrides"

        val DISTRIBUTION_FILE_MAPPER: (String) -> String = { it -> it.substringAfter("/") }
    }

    class Script(val command: List<String>, val file: File) {
        val commandLine: List<String>
            get() = command + listOf(file.absolutePath)
    }

    val logger = project.logger

    val config = JavarelConfig.of(project)

    val dir = File("${System.getProperty("user.home")}/.javarel/${config.buildName}")

    val bundleDir = File(dir, "bundle")

    val properties = mapOf(
            "instance" to instance,
            "dir" to dir
    )

    fun create(bundles: List<File>) {
        clean(true)

        extractFilesFromDistribution()
        overrideFilesUsingResources()
        expandFiles()
        copyBundles(bundles)
    }

    private fun copyBundles(bundles: List<File>) {
        bundles.forEach { FileUtils.copyFileToDirectory(it, bundleDir) }
    }

    private fun script(name: String): Script {
        return if (OperatingSystem.current().isWindows) {
            Script(listOf("cmd", "/C"), File(dir, "$name.bat"))
        } else {
            Script(listOf("sh"), File(dir, "$name.sh"))
        }
    }

    fun up() {
        execute(script("start"))
    }

    fun down() {
        execute(script("stop"))
    }

    private fun execute(script: Script) {
        ProcessBuilder(*script.commandLine.toTypedArray())
                .directory(dir)
                .start()
    }

    private fun extractFilesFromDistribution() {
        if (config.distributionUrl.isNotBlank()) {
            logger.info("Creating instance using distribution URL '${config.distributionUrl}'.")

            val distributionFile = FileResolver.single(project, config.distributionUrl)
            ZipUtil.unpack(distributionFile, dir, DISTRIBUTION_FILE_MAPPER)
        } else {
            logger.info("Creating instance using default distribution.")

            val distributionResource = FileOperations.readResource(DISTRIBUTION_RESOURCE)
            ZipUtil.unpack(distributionResource, dir, DISTRIBUTION_FILE_MAPPER)
        }

        logger.info("Instance files extracted from distribution properly.")
    }

    private fun overrideFilesUsingResources() {
        logger.info("Overriding instance files using parametrized ones.")

        FileOperations.copyResources(DISTRIBUTION_OVERRIDES, dir)
    }

    private fun expandFiles() {
        logger.info("Injecting parameters into instance files.")

        FileOperations.amendFiles(dir, config.fileExpandable, { file, source ->
            PropertyParser(project).expand(source, properties, file.absolutePath)
        })
    }

    fun clean(create: Boolean = true) {
        if (dir.exists()) {
            dir.deleteRecursively()
        }

        if (create) {
            GFileUtils.mkdirs(dir)
        }
    }

    fun destroy() {
        clean(false)
    }

}