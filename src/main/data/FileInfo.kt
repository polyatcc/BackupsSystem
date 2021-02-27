package data

import java.io.File

class FileInfo(val path: String, val size: Long) {

    constructor(path: String) : this(path, File(path).length())

    override fun equals(other: Any?): Boolean {
        other ?: return false
        if (other !is FileInfo) {
            return false
        }
        return path == other.path
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }

}