package com.neva.javarel.gradle

import com.neva.javarel.gradle.instance.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.language.base.plugins.LifecycleBasePlugin

class InstancePlugin : Plugin<Project> {

    companion object {
        val ID = "com.neva.javarel.instance"
    }

    override fun apply(project: Project) {
        setupDependentPlugins(project)
        setupOwnTasks(project)
    }

    private fun setupDependentPlugins(project: Project) {
        project.plugins.apply(BasePlugin::class.java)
    }

    private fun setupOwnTasks(project: Project) {
        val clean = project.tasks.getByName(LifecycleBasePlugin.CLEAN_TASK_NAME)

        val create = project.tasks.create(CreateTask.NAME, CreateTask::class.java)
        val destroy = project.tasks.create(DestroyTask.NAME, DestroyTask::class.java)
        val up = project.tasks.create(UpTask.NAME, UpTask::class.java)
        val down = project.tasks.create(DownTask.NAME, DownTask::class.java)
        val setup = project.tasks.create(SetupTask.NAME, SetupTask::class.java)

        create.mustRunAfter(clean)
        up.mustRunAfter(clean, create)
        setup.dependsOn(clean, create, up)
    }

}