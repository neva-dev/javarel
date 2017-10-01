package com.neva.javarel.framework.core.verticle

import io.vertx.core.AsyncResult
import io.vertx.core.DeploymentOptions
import io.vertx.core.Handler
import io.vertx.core.Verticle

interface VerticleHandler : Verticle {

    val deploymentOptions : DeploymentOptions
        get() = DeploymentOptions()

    val onDeploy: Handler<AsyncResult<String>>
        get() = Handler {  }

    val onUndeploy: Handler<AsyncResult<Void>>
        get() = Handler {  }

}