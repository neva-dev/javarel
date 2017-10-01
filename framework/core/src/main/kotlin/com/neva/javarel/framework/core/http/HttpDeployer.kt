package com.neva.javarel.framework.core.http

import com.neva.javarel.framework.core.verticle.AbstractVerticleHandler
import com.neva.javarel.framework.core.verticle.VerticleHandler
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import org.osgi.framework.BundleContext
import org.osgi.service.component.annotations.*
import java.util.concurrent.CopyOnWriteArrayList

@Component(
        service = arrayOf(HttpDeployer::class, VerticleHandler::class),
        immediate = true
)
class HttpDeployer : AbstractVerticleHandler() {

    private lateinit var server: HttpServer

    private lateinit var router: Router

    private lateinit var bundleContext: BundleContext

    @Reference(
            bind = "register",
            unbind = "unregister",
            policy = ReferencePolicy.DYNAMIC,
            service = HttpHandler::class
    )
    private val handlers: MutableList<HttpHandler> = CopyOnWriteArrayList()

    @Activate
    fun activate(bundleContext: BundleContext) {
        this.bundleContext = bundleContext
    }

    override fun start() {
        reconfigure()
    }

    override fun stop() {
        server.close()
    }

    // TODO use configuration stored in JCR
    val port: Int
        get() = (bundleContext.getProperty("jv.core.http.server.port") ?: "6661").toInt()

    protected fun register(handler: HttpHandler) {
        handlers.add(handler)
        reconfigure()
    }

    protected fun unregister(handler: HttpHandler) {
        handlers.remove(handler)
        reconfigure()
    }

    private fun reconfigure() {
        router = Router.router(vertx)
        server = vertx.createHttpServer()
        server.requestHandler({ router.accept(it) }).listen(port)

        handlers.onEach { it.configure(server, router) }
    }

}