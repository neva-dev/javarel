package com.neva.javarel.framework.core

import io.vertx.core.Vertx
import io.vertx.core.logging.LoggerFactory
import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext
import org.osgi.framework.ServiceRegistration

class Activator : BundleActivator {

    companion object {
        val log = LoggerFactory.getLogger(Activator::class.java)
    }

    private lateinit var vertxReg: ServiceRegistration<Vertx>

    override fun start(context: BundleContext) {
        log.info("Starting Javarel core")

        vertxReg = context.registerService(Vertx::class.java, Vertx.vertx(), null)
    }

    override fun stop(context: BundleContext) {
        vertxReg.unregister()

        log.info("Stopping Javarel core")
    }

}
