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

    override fun start(context: BundleContext) {
        dumpPid()

        val vertx = TcclSwitch.use { Vertx.vertx() }

        vertxReg = context.registerService(Vertx::class.java, vertx, null)
        eventBusReg = context.registerService(EventBus::class.java, vertx.eventBus(), null)
    }

    override fun stop(context: BundleContext) {
        vertxReg.unregister()
        eventBusReg.unregister()
    }

    private fun dumpPid() {
        val pid = ManagementFactory.getRuntimeMXBean().name

        File("javarel.pid").printWriter().use { it.print(pid) }
    }

}