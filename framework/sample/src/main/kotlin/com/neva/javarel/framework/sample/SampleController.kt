package com.neva.javarel.framework.sample

import com.neva.javarel.framework.core.Controller
import com.neva.javarel.framework.core.HttpContext
import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference

@Component(
        immediate = true, service = arrayOf(SampleController::class))
class SampleController : Controller() {

    @Reference
    private lateinit var httpContext: HttpContext

    @Activate
    fun activate() {
        define(httpContext.router.get("/sample/*").handler { rc ->
            rc.response().putHeader("content-type", "text/html").end("Sample!")
        })
    }

}