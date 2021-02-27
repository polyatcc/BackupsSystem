package data

import joinPaths
import java.io.File
import java.time.Instant

class LocalFileSystem(private val rootPath: String) : FileSystem {

    private val rootDirectory = File(rootPath)

    init {
        if (rootDirectory.exists()) {
            require(rootDirectory.isDirectory) { "Specified root directory should be a directory" }
        } else {
            rootDirectory.mkdirs()
        }
    }

    override fun allFiles(path: String): List<FileInfo> {
        return File("$rootDirectory/$path").walkTopDown().filter { it.isFile }.map {
            FileInfo(it.path.removePrefix(rootDirectory.path))
        }.toList()
    }

    override fun lastModified(path: String): Instant {
        return Instant.ofEpochMilli(File("$rootDirectory/$path").lastModified())
    }

    fun actualFile(path: String): File {
        return File(joinPaths(rootPath, path))
    }

}