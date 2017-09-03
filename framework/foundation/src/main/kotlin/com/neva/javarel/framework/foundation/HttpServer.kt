package com.neva.javarel.framework.foundation

import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Deactivate
import org.osgi.service.component.annotations.Reference

@Component(
        service = arrayOf(HttpServer::class),
        immediate = true
)
class HttpServer {

    @Reference
    private lateinit var vertx: Vertx

    private lateinit var server: HttpServer

    @Activate
    fun start() {
        val server = TcclSwitch.use({ vertx.createHttpServer() })

        server.requestHandler({ request ->
            request.response().end("Hello from Javarel!")
        }).listen(6661)
    }

    @Deactivate
    fun stop() {
        server.close()
    }

}