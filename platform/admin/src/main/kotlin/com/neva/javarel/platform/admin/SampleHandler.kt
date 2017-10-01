package com.neva.javarel.platform.admin

import com.neva.javarel.framework.core.http.HttpHandler
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import org.osgi.service.component.annotations.Component

@Component(
        immediate = true,
        service = arrayOf(HttpHandler::class)
)
class SampleHandler : HttpHandler {

    override fun configure(httpServer: HttpServer, router: Router) {
        router.get("/sample/*").handler { rc ->
            rc.response().putHeader("content-type", "text/html").end("Sample!")
        }
    }

}