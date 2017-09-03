package com.neva.javarel.framework.foundation

import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext
import org.osgi.framework.ServiceRegistration
import java.io.File
import java.lang.management.ManagementFactory

class Activator : BundleActivator {

    private lateinit var vertxReg: ServiceRegistration<Vertx>

    private lateinit var eventBusReg: ServiceRegistration<EventBus>

    private val pidFile = File("pid.lock")

    override fun start(context: BundleContext) {
        createPidFile()

        val vertx = TcclSwitch.use { Vertx.vertx() }

        vertxReg = context.registerService(Vertx::class.java, vertx, null)
        eventBusReg = context.registerService(EventBus::class.java, vertx.eventBus(), null)
    }

    override fun stop(context: BundleContext) {
        vertxReg.unregister()
        eventBusReg.unregister()

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
