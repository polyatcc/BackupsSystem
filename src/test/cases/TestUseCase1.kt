package cases

import Test
import backup.Backup
import backup.RestorePoint
import backup.clean.LongerThan
import data.LocalFileSystem
import storage.FileStorageProvider

object TestUseCase1 : Test("Use case 1") {

    override val path = "resources-bak/test-1"

    private val storage = FileStorageProvider(path)
    private val fileSystem = LocalFileSystem("resources")
    private val testFileSystem = LocalFileSystem(path)

    override fun run() {
        super.run()
        val backup = Backup(storage, fileSystem)

        backup.addPath("bar/a")
        backup.addPath("foo/c")
        backup.createRestorePoint(RestorePoint.Type.Complete)

        val backupFiles = testFileSystem.allFiles("")
        require(backupFiles.size == 2) { "Should have two backed up files" }
        println("Ok, got [${backupFiles.size}] files in backup")

        backup.createRestorePoint(RestorePoint.Type.Complete)

        backup.clean(LongerThan(1))
        require(backup.restorePointsCount == 1) { "Expected only one restore point to remain" }
        println("Ok, got [${backup.restorePointsCount}] restore points in backup")
    }

}