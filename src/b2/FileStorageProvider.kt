package storage

import backup.Backup
import backup.RestorePoint
import joinPaths
import java.io.File
import java.io.PrintWriter

class FileStorageProvider(private val rootPath: String) : StorageProvider {

    override fun requestStorage(backup: Backup) = object : Storage {
        val backupRootPath = joinPaths(rootPath, backup.backupId.toString())

        private fun storeFile(filePath: String, message: PrintWriter.() -> Unit) {
            val file = File(filePath)
            file.parentFile.mkdirs()
            file.createNewFile()
            file.printWriter().use(message)
        }

        override fun store(restorePoint: RestorePoint) {
            val restorePointPath = joinPaths(backupRootPath, restorePoint.timestampPath)
            restorePoint.files.forEach {
                val filePath = joinPaths(restorePointPath, it.path)
                storeFile(filePath) {
                    println("File '${it.path}' is backed up in '${restorePointPath}'")
                    println(" - with specified restore point timestamp")
                }
            }
        }

        override fun erase(restorePoint: RestorePoint) {
            val restorePointPath = joinPaths(backupRootPath, restorePoint.timestampPath)
            File(restorePointPath).deleteRecursively()
        }
    }

}