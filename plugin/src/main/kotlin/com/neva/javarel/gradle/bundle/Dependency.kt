package com.neva.javarel.gradle.bundle

import java.io.File
import java.io.Serializable
import org.gradle.api.artifacts.Dependency as Base

data class Dependency(val group: String, val name: String, val version: String) : Serializable {

    companion object {

        fun from(base: Base): Dependency {
            return Dependency(base.group, base.name, base.version)
        }

    }

    val relativePath: String
        get() = "${group.replace(".", "/")}/$name/$version".replace("/", File.separator)

}