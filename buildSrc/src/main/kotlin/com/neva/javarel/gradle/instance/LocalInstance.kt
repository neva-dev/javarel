package com.neva.javarel.gradle.instance

class LocalInstance(override val httpUrl: String, override val name: String) : Instance {

    companion object {
        val ENV = "local"
    }

    override val env: String
        get() = ENV

}