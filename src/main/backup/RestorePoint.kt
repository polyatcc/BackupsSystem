package backup

import data.FileInfo
import data.FileSystem
import storage.Storage
import java.time.Instant

sealed class RestorePoint(fileInfos: Iterable<FileInfo>) {

    enum class Type {
        Complete,
        Incremental
    }

    val creationTime: Instant = Instant.now()
    val files = fileInfos.toMutableSet()

    val size: Long get() = files.map { it.size }.sum()

    abstract val timestampPath: String

}

class CompleteRestorePoint(fileInfos: Iterable<FileInfo>) : RestorePoint(fileInfos) {

    override val timestampPath: String
        get() = Storage.fixPath("$creationTime [complete!]")

}

class IncrementalRestorePoint private constructor(fileInfos: Iterable<FileInfo>) : RestorePoint(fileInfos) {

    companion object {
        operator fun invoke(
            referencePoint: RestorePoint,
            backupFileInfos: Iterable<FileInfo>,
            uncommitedFileInfos: Iterable<FileInfo>,
            fileSystem: FileSystem
        ): IncrementalRestorePoint {
            val modifiedFileInfos = backupFileInfos.filter {
                fileSystem.lastModified(it.path) > referencePoint.creationTime
            }
            return IncrementalRestorePoint(modifiedFileInfos union uncommitedFileInfos)
        }
    }

    override val timestampPath: String
        get() = Storage.fixPath("$creationTime")

}

typealias RestorePointChain = ArrayList<RestorePoint>