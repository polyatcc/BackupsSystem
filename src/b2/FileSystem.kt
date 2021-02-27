package data

import java.io.File
import java.time.Instant

interface FileSystem {

    fun allFiles(path: String): List<FileInfo>

    fun lastModified(path: String): Instant

}