package com.neva.javarel.framework.core

import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import org.osgi.framework.BundleContext
import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Deactivate
import org.osgi.service.component.annotations.Reference

@Component(
        service = arrayOf(HttpContext::class),
        immediate = true
)
class HttpContext {

    @Reference
    private lateinit var vertx: Vertx

    private lateinit var _server: HttpServer

    private lateinit var _router: Router

    private lateinit var bundleContext: BundleContext

    @Reference(unbind = "clearRoutes")
    private lateinit var controllers: List<HttpController>

    @Activate
    fun start(bundleContext: BundleContext) {
        this.bundleContext = bundleContext

        _router = Router.router(vertx)
        _server = TcclSwitch.use({ vertx.createHttpServer() })
        _server.requestHandler({ _router.accept(it) }).listen(port)
    }

    @Deactivate
    fun stop() {
        _server.close()
    }

    val port: Int
        get() = (bundleContext.getProperty("jv.core.http.server.port") ?: "6661").toInt()

    val server: HttpServer
        get() = _server

    val router: Router
        get() = _router

    fun clearRoutes(controller: HttpController) {
        _router.routes.removeAll(controller.routes)
    }

}