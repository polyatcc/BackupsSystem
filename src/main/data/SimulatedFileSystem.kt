package data

import java.io.File
import java.time.Instant

object SimulatedFileSystem : FileSystem {

    private val files = mapOf(
        "a/b/c" to FileInfo("a/b/c", 100L shl 20),
        "a/b/x" to FileInfo("a/b/x", 100L shl 20),
        "a/x.txt" to FileInfo("a/x.txt", 2L shl 20),
        "b/a/c/y.mp3" to FileInfo("b/a/c/y.mp3", 1L shl 30)
    )

    override fun allFiles(path: String): List<FileInfo> {
        return files.entries.filter {
            it.key.startsWith(path)
        }.map { it.value }
    }

    override fun lastModified(path: String): Instant {
        return if (path.startsWith("b")) Instant.now() else Instant.EPOCH
    }

}