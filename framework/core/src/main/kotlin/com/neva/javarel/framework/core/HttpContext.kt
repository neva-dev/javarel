package com.neva.javarel.framework.core

import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import org.osgi.framework.BundleContext
import org.osgi.service.component.annotations.*
import java.util.concurrent.CopyOnWriteArrayList

@Component(
        service = arrayOf(HttpContext::class),
        immediate = true
)
class HttpContext {

    @Reference
    private lateinit var vertx: Vertx

    private lateinit var server: HttpServer

    private lateinit var router: Router

    private lateinit var bundleContext: BundleContext

    @Reference(
            bind = "register",
            unbind = "unregister",
            policy = ReferencePolicy.DYNAMIC,
            service = HttpHandler::class
    )
    private val controllers: MutableList<HttpHandler> = CopyOnWriteArrayList()

    @Activate
    fun start(bundleContext: BundleContext) {
        this.bundleContext = bundleContext
    }

    @Deactivate
    fun stop() {
        server.close()
    }

    val port: Int
        get() = (bundleContext.getProperty("jv.core.http.server.port") ?: "6661").toInt()

    protected fun register(controller: HttpHandler) {
        controllers.add(controller)
        reconfigure()
    }

    protected fun unregister(controller: HttpHandler) {
        controllers.remove(controller)
        reconfigure()
    }

    private fun reconfigure() {
        router = Router.router(vertx)
        server = TcclSwitch.use({ vertx.createHttpServer() })
        server.requestHandler({ router.accept(it) }).listen(port)

        controllers.onEach { it.configure(server, router) }
    }

}