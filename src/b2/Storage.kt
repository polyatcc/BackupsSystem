package storage

import backup.Backup
import backup.RestorePoint

interface Storage {

    companion object {
        fun fixPath(path: String): String =
            path.replace(':', '-')
    }

    fun store(restorePoint: RestorePoint)

    fun erase(restorePoint: RestorePoint)

}

interface StorageProvider {
    fun requestStorage(backup: Backup): Storage
}
