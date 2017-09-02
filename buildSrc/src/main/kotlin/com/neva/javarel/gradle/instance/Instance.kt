package com.neva.javarel.gradle.instance

import java.io.Serializable
import java.net.URL

interface Instance : Serializable {

    val httpUrl: String

    val httpPort: Int
        get() = URL(httpUrl).port

    val name: String

    val env: String

    val id: String
        get() = "$env-$name"

}
