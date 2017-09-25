package com.neva.javarel.framework.launcher

import org.apache.felix.framework.FrameworkFactory
import org.apache.felix.main.AutoProcessor
import org.apache.felix.main.Main
import org.apache.felix.main.Main.SHUTDOWN_HOOK_PROP
import org.osgi.framework.launch.Framework
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

object Launcher {

    lateinit var m_fwk: Framework

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        println("\nJavarel")
        println("======================\n")

        Main.loadSystemProperties()

        val configProps = Properties()
        configProps.putAll(Main.loadConfigProperties())
        Main.copySystemProperties(configProps)

        val enableHook = configProps.getProperty(SHUTDOWN_HOOK_PROP)
        if (enableHook == null || !enableHook.equals("false", ignoreCase = true)) {
            Runtime.getRuntime().addShutdownHook(object : Thread("Felix Shutdown Hook") {
                override fun run() {
                    try {
                        if (m_fwk != null) {
                            m_fwk.stop()
                            m_fwk.waitForStop(0)
                        }
                    } catch (ex: Exception) {
                        System.err.println("Error stopping framework: " + ex)
                    }

                }
            })
        }

        try {
            val factory = getFrameworkFactory()
            m_fwk = factory.newFramework(configProps)
            m_fwk.init()
            AutoProcessor.process(configProps, m_fwk.getBundleContext())
            m_fwk.start()
            m_fwk.waitForStop(0)
            System.exit(0)
        } catch (ex: Exception) {
            System.err.println("Could not create framework: " + ex)
            ex.printStackTrace()
            System.exit(0)
        }

    }

    private fun getFrameworkFactory(): FrameworkFactory {
        val url = Main::class.java.classLoader.getResource(
                "META-INF/services/org.osgi.framework.launch.FrameworkFactory")
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