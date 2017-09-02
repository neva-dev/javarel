package com.neva.javarel.gradle

import com.neva.javarel.gradle.instance.CreateTask
import com.neva.javarel.gradle.instance.DestroyTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class InstancePlugin : Plugin<Project> {

    companion object {
        val ID = "com.neva.javarel.instance"
    }

    override fun apply(project: Project) {
        project.plugins.apply(BasePlugin::class.java)

        project.tasks.create(CreateTask.NAME, CreateTask::class.java)
        project.tasks.create(DestroyTask.NAME, DestroyTask::class.java)
    }

}