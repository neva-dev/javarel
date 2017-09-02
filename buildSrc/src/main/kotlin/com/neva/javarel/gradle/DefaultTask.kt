package com.neva.javarel.gradle

import org.gradle.api.tasks.Nested
import org.gradle.api.DefaultTask as BaseTask

open class DefaultTask : BaseTask(), JavarelTask {

    @get:Nested
    override val config: JavarelConfig
        get() = JavarelConfig.of(project)

    init {
        group = JavarelTask.GROUP
    }

}