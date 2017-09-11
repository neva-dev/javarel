package com.neva.javarel.framework.core

import io.vertx.core.Vertx
import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext
import org.osgi.framework.ServiceRegistration
import java.io.File
import java.lang.management.ManagementFactory

class Activator : BundleActivator {

    private lateinit var vertxReg: ServiceRegistration<Vertx>

    private val pidFile = File("pid.lock")

    override fun start(context: BundleContext) {
        createPidFile()

        val vertx = TcclSwitch.use { Vertx.vertx() }

        vertxReg = context.registerService(Vertx::class.java, vertx, null)
    }

    override fun stop(context: BundleContext) {
        vertxReg.unregister()

        deletePidFile()
    }

    private fun createPidFile() {
        val pid = ManagementFactory.getRuntimeMXBean().name.substringBefore("@").toInt()

        pidFile.printWriter().use { it.print(pid) }
    }

    private fun deletePidFile() {
        if (pidFile.exists()) {
            pidFile.delete()
        }
    }

}
