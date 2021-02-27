import backup.Backup
import backup.RestorePoint
import data.LocalFileSystem
import storage.ArchiveStorageProvider
import storage.FileStorageProvider

fun main() {

//    val storage = ArchiveStorageProvider("resources-bak")
    val storage = FileStorageProvider("resources-bak")

    val backup = Backup(
        storage,
        LocalFileSystem("resources")
    )

    backup.addPath("bar/a")
    backup.addPath("foo/b")
    backup.createRestorePoint(RestorePoint.Type.Complete)

    backup.addPath("foo/c")
    backup.createRestorePoint(RestorePoint.Type.Incremental)

    backup.createRestorePoint(RestorePoint.Type.Complete)

}