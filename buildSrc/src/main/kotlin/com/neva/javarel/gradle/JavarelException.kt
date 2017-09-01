package com.neva.javarel.gradle

import org.gradle.api.GradleException

class JavarelException : GradleException {

    constructor(message: String, e: Throwable) : super(message, e)

    constructor(message: String) : super(message)


}