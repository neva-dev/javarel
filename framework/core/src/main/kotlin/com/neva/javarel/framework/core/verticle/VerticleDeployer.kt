package com.neva.javarel.framework.core.verticle

import io.vertx.core.Vertx
import org.osgi.service.component.annotations.*
import java.util.concurrent.ConcurrentHashMap

@Component(immediate = true)
class VerticleDeployer {

    @Reference
    private lateinit var vertx: Vertx

    private var deploymentIds = ConcurrentHashMap<VerticleHandler, String>()

    @Reference(
            cardinality = ReferenceCardinality.MULTIPLE,
            service = com.neva.javarel.framework.core.verticle.VerticleHandler::class,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unbindHandler"
    )
    protected fun bindHandler(handler: VerticleHandler) {
        vertx.deployVerticle(handler, handler.deploymentOptions, { ar ->
            if (ar.succeeded()) {
                deploymentIds.put(handler, ar.result())
            }
            handler.onDeploy.handle(ar)
        })
    }

    protected fun unbindVerticle(handler: VerticleHandler) {
        vertx.undeploy(deploymentIds[handler], { ar ->
            if (ar.succeeded()) {
                deploymentIds.remove(handler)
            }
            handler.onUndeploy.handle(ar)
        })
    }

    @Deactivate
    protected fun stop() {
        deploymentIds.values.forEach { vertx.undeploy(it) }
    }

}