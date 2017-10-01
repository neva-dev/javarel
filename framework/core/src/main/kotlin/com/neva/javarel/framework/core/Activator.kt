package com.neva.javarel.framework.core

import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager
import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext
import org.osgi.framework.ServiceRegistration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.apache.jackrabbit.oak.Oak
import org.apache.jackrabbit.oak.jcr.Jcr
import org.apache.jackrabbit.oak.segment.SegmentNodeStoreBuilders
import org.apache.jackrabbit.oak.segment.file.FileStoreBuilder
import java.io.File
import javax.jcr.Repository


class Activator : BundleActivator {

    private var vertxReg: ServiceRegistration<Vertx>? = null

    private var repoReg: ServiceRegistration<Repository>? = null

    // TODO store all json config in JCR
    override fun start(context: BundleContext) {
        val options = VertxOptions()
        options.clusterManager = HazelcastClusterManager()
        options.isClustered = true

        val latch = CountDownLatch(1)
        Vertx.clusteredVertx(options, { ar ->
            vertxReg = context.registerService(Vertx::class.java, ar.result(), null)
            latch.countDown()
        })

        latch.await(1, TimeUnit.MINUTES)

        val fs = FileStoreBuilder.fileStoreBuilder(File("repository")).build()
        val ns = SegmentNodeStoreBuilders.builder(fs).build()
        val repo = Jcr(Oak(ns)).createRepository()

        repoReg = context.registerService(Repository::class.java, repo, null)
    }

    override fun stop(context: BundleContext) {
        vertxReg?.unregister()
        repoReg?.unregister()
    }

}
