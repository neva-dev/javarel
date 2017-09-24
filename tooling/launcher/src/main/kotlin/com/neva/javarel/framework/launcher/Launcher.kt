package com.neva.javarel.framework.launcher

import org.apache.felix.framework.FrameworkFactory
import org.apache.felix.main.AutoProcessor
import java.io.InputStreamReader
import java.io.BufferedReader

object Launcher {

    @JvmStatic
    fun main(argv: Array<String>) {
        println("\nWelcome to Javarel")
        println("======================\n")

        try {
            val framework = createFrameworkFactory().newFramework(null)
            framework.init()
            AutoProcessor.process(null, framework.bundleContext)
            framework.start()
            framework.waitForStop(0)

            System.exit(0)
        } catch (e: Exception) {
            System.err.println("Could not create framework: " + e)

            e.printStackTrace()
            System.exit(-1)
        }
    }

    private fun createFrameworkFactory(): FrameworkFactory {
        val url = Launcher::class.java.classLoader.getResource(
                "META-INF/services/" + org.osgi.framework.launch.FrameworkFactory::class.java.canonicalName)
        if (url != null) {
            val br = BufferedReader(InputStreamReader(url.openStream()))
            br.use { reader ->
                var s: String? = reader.readLine()
                while (s != null) {
                    s = s.trim { it <= ' ' }
                    if (s.isNotEmpty() && s[0] != '#') {
                        return Class.forName(s).newInstance() as FrameworkFactory
                    }
                    s = reader.readLine()
                }
            }
        }

        throw Exception("Could not find framework factory.")
    }

}