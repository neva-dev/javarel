package com.neva.javarel.gradle.instance

import java.io.Serializable
import java.net.URL

interface Instance : Serializable {

    val httpUrl: String

    val name: String

    val env: String

    val id: String
        get() = "$env-$name"

    val httpPort: Int
        get() = URL(httpUrl).port

    val debugPort: Int
        get() = "1$httpPort".toInt()

    val jmxPort: Int
        get() = "2$httpPort".toInt()

}
