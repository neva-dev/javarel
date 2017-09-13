package com.neva.javarel.gradle

import org.gradle.api.Project
import org.gradle.util.GFileUtils
import java.io.File

interface JavarelTask {

    val config: JavarelConfig

    companion object {
        val GROUP = "Javarel"

        val CATEGORY = "javarel"

        fun temporaryDir(project: Project): File {
            return temporaryDir(project, "tmp")
        }

        fun temporaryDir(project: Project, taskName: String, path: String): File {
            return temporaryDir(project, "$taskName/$path")
        }

        fun temporaryDir(project: Project, path: String): File {
            val dir = File(project.buildDir, "$CATEGORY/$path")

            GFileUtils.mkdirs(dir)

            return dir
        }

        fun temporaryFile(project: Project, taskName: String, name: String): File {
            val dir = File(project.buildDir, "$CATEGORY/$taskName")

            GFileUtils.mkdirs(dir)

            return File(dir, name)
        }
    }

}