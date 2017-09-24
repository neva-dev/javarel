package com.neva.javarel.gradle.bundle

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.File
import java.io.Serializable
import org.gradle.api.artifacts.Dependency as Base

@JsonIgnoreProperties(ignoreUnknown = true)
class Dependency private constructor() : Serializable {

    companion object {

        fun from(base: Base): Dependency {
            val result = Dependency()

            result.group = base.group
            result.name = base.name
            result.version = base.version

            return result
        }

    }

    lateinit var group: String

    lateinit var name: String

    lateinit var version: String

    @get:JsonIgnore
    val mvnPath: String
        get() = "${group.replace(".", "/")}/$name/$version".replace("/", File.separator)

    @get:JsonIgnore
    val gradlePath: String
        get() = "$group/$name/$version".replace("/", File.separator)

    @get:JsonIgnore
    val searchPaths
        get() = listOf(mvnPath, gradlePath)

    fun matches(jar: File): Boolean {
        return searchPaths.any { jar.absolutePath.contains(it) }
    }

}
