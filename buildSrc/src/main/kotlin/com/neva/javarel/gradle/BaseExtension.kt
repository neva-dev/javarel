package com.neva.javarel.gradle

import com.neva.javarel.gradle.instance.Instance
import org.gradle.api.Project

open class BaseExtension(project: Project) {

    companion object {
        val NAME = "javarel";
    }

    var instances: MutableList<Instance> = mutableListOf()

    fun localInstance(url: String) {
        instances.add(Instance(url))
    }

}