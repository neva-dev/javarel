package com.neva.javarel.framework.foundation

object TcclSwitch {

    fun <T> use(action: () -> T): T {
        val original = Thread.currentThread().contextClassLoader
        try {
            Thread.currentThread().contextClassLoader = TcclSwitch::class.java.classLoader
            return action()
        } finally {
            Thread.currentThread().contextClassLoader = original
        }
    }

}