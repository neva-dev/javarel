package com.neva.javarel.framework.core.http

import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router

interface HttpHandler {

    fun configure(httpServer: HttpServer, router: Router)

}