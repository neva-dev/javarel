package com.neva.javarel.framework.core

import io.vertx.ext.web.Route


// configure(httpserver) , instead; reconfigure all if any changed.
abstract class Controller : HttpController {

    private val _routes: MutableList<Route> = mutableListOf()

    override val routes: List<Route>
        get() = _routes

    fun define(route: Route) {
        _routes.add(route)
    }

}