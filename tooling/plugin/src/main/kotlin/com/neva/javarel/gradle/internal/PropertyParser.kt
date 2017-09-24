package com.neva.javarel.gradle.internal

import com.mitchellbosecke.pebble.PebbleEngine
import com.mitchellbosecke.pebble.lexer.Syntax
import com.mitchellbosecke.pebble.loader.StringLoader
import com.neva.javarel.gradle.JavarelConfig
import com.neva.javarel.gradle.JavarelException
import org.apache.commons.lang3.BooleanUtils
import org.apache.commons.lang3.ClassUtils
import org.apache.commons.lang3.text.StrSubstitutor
import org.gradle.api.Project
import java.io.StringWriter

class PropertyParser(val project: Project) {

    companion object {

        const val FILTER_DEFAULT = "*"

        const val FORCE_PROP = "jv.force"

        private val TEMPLATE_VAR_PREFIX = "{{"

        private val TEMPLATE_VAR_SUFFIX = "}}"

        private val TEMPLATE_ENGINE = PebbleEngine.Builder()
                .autoEscaping(false)
                .cacheActive(false)
                .strictVariables(true)
                .newLineTrimming(false)
                .loader(StringLoader())
                .syntax(Syntax.Builder()
                        .setPrintOpenDelimiter(TEMPLATE_VAR_PREFIX)
                        .setPrintCloseDelimiter(TEMPLATE_VAR_SUFFIX)
                        .build()
                )
                .build()

        private val TEMPLATE_INTERPOLATOR: (String, Map<String, Any>) -> String = { source, props ->
            StrSubstitutor.replace(source, props, TEMPLATE_VAR_PREFIX, TEMPLATE_VAR_SUFFIX)
        }

    }

    private val config = JavarelConfig.of(project)

    fun prop(name: String): String? {
        var value = project.properties[name] as String?
        if (value == null) {
            value = systemProperties[name]
        }

        return value
    }

    fun prop(name: String, defaultValue: () -> String): String {
        return prop(name) ?: defaultValue()
    }

    fun filter(value: String, propName: String, propDefault: String = FILTER_DEFAULT): Boolean {
        val filters = project.properties.getOrElse(propName, { propDefault }) as String

        return filters.split(",").any { group -> Patterns.wildcard(value, group) }
    }

    fun expand(source: String, properties: Map<String, Any> = mapOf(), context: String? = null): String {
        try {
            val interpolableProperties = systemProperties + configProperties.filterValues {
                it is String || ClassUtils.isPrimitiveOrWrapper(it.javaClass)
            }
            val interpolated = TEMPLATE_INTERPOLATOR(source, interpolableProperties)

            val templateProperties = projectProperties + defaultProperties + configProperties + properties
            val expanded = StringWriter()

            TEMPLATE_ENGINE.getTemplate(interpolated).evaluate(expanded, templateProperties)

            return expanded.toString()
        } catch (e: Throwable) {
            var msg = "Cannot expand properly all properties. Probably used non-existing field name or unescaped char detected. Source: '${source.trim()}'."
            if (!context.isNullOrBlank()) msg += " Context: $context"
            throw JavarelException(msg, e)
        }
    }

    val systemProperties: Map<String, String> by lazy {
        System.getProperties().entries.fold(mutableMapOf<String, String>(), { props, prop ->
            props.put(prop.key.toString(), prop.value.toString()); props
        })
    }

    val projectProperties: Map<String, Any>
        get() = mapOf(
                "rootProject" to project.rootProject,
                "project" to project
        )

    val defaultProperties: Map<String, Any>
        get() {
            return mapOf(
                    "config" to config,
                    "requiresRoot" to "false",
                    "created" to Formats.date(config.buildDate)
            )
        }

    val configProperties: Map<String, Any>
        get() = JavarelConfig.of(project).fileProperties

    fun checkForce(message: String = "") {
        if (!project.properties.containsKey(FORCE_PROP) || !BooleanUtils.toBoolean(project.properties[FORCE_PROP] as String?)) {
            throw JavarelException("Warning! This task execution must be confirmed by specifying explicitly parameter '-P${FORCE_PROP}=true'. $message")
        }
    }

    fun checkOffline(): Boolean {
        return project.gradle.startParameter.isOffline
    }

}
