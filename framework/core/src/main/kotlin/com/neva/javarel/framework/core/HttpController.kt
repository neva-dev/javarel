package com.neva.javarel.framework.core

import io.vertx.ext.web.Route

interface HttpController {

    val routes: List<Route>

}