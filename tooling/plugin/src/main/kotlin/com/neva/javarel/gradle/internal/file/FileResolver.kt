package com.neva.javarel.gradle.internal.file

import com.google.common.hash.HashCode
import com.neva.javarel.gradle.JavarelTask
import com.neva.javarel.gradle.internal.Formats
import groovy.lang.Closure
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang3.BooleanUtils
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.util.ConfigureUtil
import org.gradle.util.GFileUtils
import java.io.File

/**
 * Generic file downloader with groups supporting files from local and remote sources (SFTP, SMB, HTTP).
 */
class FileResolver(val project: Project, val downloadDir: File) {

    companion object {
        val GROUP_DEFAULT = "default"

        val DOWNLOAD_LOCK = "download.lock"

        fun single(project: Project, url: String): File {
            return FileResolver(project, JavarelTask.temporaryDir(project))
                    .url(url).allFiles().first()
        }
    }

    private inner class Resolver(val id: String, val group: String, val action: (Resolver) -> File) {
        val dir = File("$downloadDir/$id")

        val file: File by lazy { action(this) }
    }

    private val resolvers = mutableListOf<Resolver>()

    private var group: String = GROUP_DEFAULT

    val configured: Boolean
        get() = resolvers.isNotEmpty()

    val configurationHash: Int
        get() {
            val builder = HashCodeBuilder()
            resolvers.forEach { builder.append(it.id) }

            return builder.toHashCode()
        }

    fun attach(task: DefaultTask, prop: String = "fileResolver") {
        task.outputs.dir(downloadDir)
        project.afterEvaluate {
            task.inputs.property(prop, configurationHash)
        }
    }

    fun outputDirs(filter: (String) -> Boolean = { true }): List<File> {
        return filterResolvers(filter).map { it.dir }
    }

    fun allFiles(filter: (String) -> Boolean = { true }): List<File> {
        return filterResolvers(filter).map { it.file }
    }

    fun groupedFiles(filter: (String) -> Boolean = { true }): Map<String, List<File>> {
        return filterResolvers(filter).fold(mutableMapOf<String, MutableList<File>>(), { files, resolver ->
            files.getOrPut(resolver.group, { mutableListOf() }).add(resolver.file); files
        })
    }

    private fun filterResolvers(filter: (String) -> Boolean): List<Resolver> {
        return resolvers.filter { filter(it.group) }
    }

    fun url(url: String): FileResolver {
        return when {
            SftpFileDownloader.handles(url) -> downloadSftpAuth(url)
            SmbFileDownloader.handles(url) -> downloadSmbAuth(url)
            HttpFileDownloader.handles(url) -> downloadHttpAuth(url)
            UrlFileDownloader.handles(url) -> downloadUrl(url)
            else -> local(url)
        }
    }

    fun downloadSftp(url: String): FileResolver {
        return resolve(url, {
            download(url, it.dir, { file ->
                SftpFileDownloader(project).download(url, file)
            })
        })
    }

    private fun download(url: String, targetDir: File, downloader: (File) -> Unit): File {
        GFileUtils.mkdirs(targetDir)

        val file = File(targetDir, FilenameUtils.getName(url))
        val lock = File(targetDir, DOWNLOAD_LOCK)
        if (!lock.exists() && file.exists()) {
            file.delete()
        }

        if (!file.exists()) {
            downloader(file)

            lock.printWriter().use {
                it.print(Formats.toJson(mapOf(
                        "downloaded" to Formats.date()
                )))
            }
        }

        return file
    }

    fun downloadSftpAuth(url: String, username: String? = null, password: String? = null, hostChecking: Boolean? = null): FileResolver {
        return resolve(url, {
            download(url, it.dir, { file ->
                val downloader = SftpFileDownloader(project)

                downloader.username = username ?: project.properties["jv.sftp.username"] as String?
                downloader.password = password ?: project.properties["jv.sftp.password"] as String?
                downloader.hostChecking = hostChecking ?: BooleanUtils.toBoolean(project.properties["jv.sftp.hostChecking"] as String? ?: "false")

                downloader.download(url, file)
            })
        })
    }

    fun downloadSmb(url: String): FileResolver {
        return resolve(url, {
            download(url, it.dir, { file ->
                SmbFileDownloader(project).download(url, file)
            })
        })
    }

    fun downloadSmbAuth(url: String, domain: String? = null, username: String? = null, password: String? = null): FileResolver {
        return resolve(url, {
            download(url, it.dir, { file ->
                val downloader = SmbFileDownloader(project)

                downloader.domain = domain ?: project.properties["jv.smb.domain"] as String?
                downloader.username = username ?: project.properties["jv.smb.username"] as String?
                downloader.password = password ?: project.properties["jv.smb.password"] as String?

                downloader.download(url, file)
            })
        })
    }

    fun downloadHttp(url: String): FileResolver {
        return resolve(url, {
            download(url, it.dir, { file ->
                HttpFileDownloader(project).download(url, file)
            })
        })
    }

    fun downloadHttpAuth(url: String, user: String? = null, password: String? = null, ignoreSSL: Boolean? = null): FileResolver {
        return resolve(arrayOf(url, user, password), {
            download(url, it.dir, { file ->
                val downloader = HttpFileDownloader(project)

                downloader.username = user ?: project.properties["jv.http.username"] as String?
                downloader.password = password ?: project.properties["jv.http.password"] as String?
                downloader.ignoreSSLErrors = ignoreSSL ?: BooleanUtils.toBoolean(project.properties["jv.http.ignoreSSL"] as String? ?: "true")

                downloader.download(url, file)
            })
        })
    }

    fun downloadUrl(url: String): FileResolver {
        return resolve(url, {
            download(url, it.dir, { file ->
                UrlFileDownloader(project).download(url, file)
            })
        })
    }

    fun local(path: String): FileResolver {
        return local(project.file(path))
    }

    fun local(sourceFile: File): FileResolver {
        return resolve(sourceFile.absolutePath, { sourceFile })
    }

    private fun resolve(hash: Any, resolver: (Resolver) -> File): FileResolver {
        val id = HashCode.fromInt(HashCodeBuilder().append(hash).toHashCode()).toString()
        resolvers += Resolver(id, group, resolver)

        return this
    }

    @Synchronized
    fun group(name: String, configurer: Closure<*>) {
        group = name
        ConfigureUtil.configureSelf(configurer, this)
        group = GROUP_DEFAULT
    }

}