package storage

import backup.Backup
import backup.RestorePoint
import joinPaths
import java.io.File

class ArchiveStorageProvider(val rootPath: String) : StorageProvider {

    override fun requestStorage(backup: Backup) = object : Storage {
        val backupRootPath = joinPaths(rootPath, backup.backupId.toString())

        init {
            File(backupRootPath).mkdirs()
        }

        override fun store(restorePoint: RestorePoint) {
            val restorePointPath = joinPaths(backupRootPath, restorePoint.timestampPath)
            val restorePointFile = File(restorePointPath)
            restorePointFile.createNewFile()

            restorePoint.files.forEach {
                restorePointFile.appendText("File '${it.path}' is backed up in '${restorePointPath}'\n")
            }
        }

        override fun erase(restorePoint: RestorePoint) {
            val restorePointPath = joinPaths(backupRootPath, restorePoint.timestampPath)
            File(restorePointPath).delete()
        }
    }

}