package backup

import backup.clean.Cleaner
import data.FileInfo
import data.FileSystem
import storage.StorageProvider
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayDeque

class Backup(storageProvider: StorageProvider, val fileSystem: FileSystem) {

    val backupId: UUID = UUID.randomUUID()
    val creationTime: Instant = Instant.now()

    private val restorePointChains = ArrayDeque<RestorePointChain>()
    private val uncommitedFileInfos = mutableSetOf<FileInfo>()
    private val backupFileInfos = mutableSetOf<FileInfo>()
    var totalSize = 0L
        private set
    val restorePointsCount: Int
        get() = restorePointChains.sumBy { it.size }

    private val storage = storageProvider.requestStorage(this)

    fun createRestorePoint(type: RestorePoint.Type) {
        when (type) {
            RestorePoint.Type.Complete -> {
                val newRestorePoint = CompleteRestorePoint(backupFileInfos union uncommitedFileInfos)
                restorePointChains.add(arrayListOf(newRestorePoint))
            }
            RestorePoint.Type.Incremental -> {
                if (restorePointChains.isEmpty()) {
                    error("Invalid usage of `createRestorePoint`: incremental RP should have a reference")
                }
                val lastChain = restorePointChains.last()
                val newRestorePoint = IncrementalRestorePoint(
                    lastChain.last(),
                    backupFileInfos,
                    uncommitedFileInfos,
                    fileSystem
                )
                lastChain.add(newRestorePoint)
            }
        }
        backupFileInfos.addAll(uncommitedFileInfos)
        uncommitedFileInfos.clear()

        val newRestorePoint = restorePointChains.last().last()
        storage.store(newRestorePoint)
        totalSize += newRestorePoint.size
    }

    fun addPath(path: String) {
        val fileInfos = fileSystem.allFiles(path)
        fileInfos.forEach {
            if (it !in backupFileInfos) {
                uncommitedFileInfos.add(it)
            }
        }
    }

    fun softErasePath(path: String) {
        backupFileInfos.remove(FileInfo(path))
        uncommitedFileInfos.remove(FileInfo(path))
    }

    private fun updateStats() {
        backupFileInfos.clear()
        totalSize = 0

        restorePointChains.forEach {
            it.forEach { restorePoint ->
                backupFileInfos.addAll(restorePoint.files)
                totalSize += restorePoint.size
            }
        }
    }

    fun clean(cleaner: Cleaner) {
        var relevant = cleaner.countRelevant(restorePointChains)
        if (relevant == 0) {
            println("Invalid usage of clean: the newest chain does not fit the requirements")
            relevant = 1
        }
        while (restorePointChains.size > relevant) {
            restorePointChains.first().forEach {
                storage.erase(it)
            }
            restorePointChains.removeFirst()
        }
        updateStats()
    }

}