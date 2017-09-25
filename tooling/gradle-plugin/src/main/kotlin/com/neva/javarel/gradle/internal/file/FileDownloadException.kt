package com.neva.javarel.gradle.internal.file

import com.neva.javarel.gradle.JavarelException

class FileDownloadException : JavarelException {

    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(message: String) : super(message)

}
