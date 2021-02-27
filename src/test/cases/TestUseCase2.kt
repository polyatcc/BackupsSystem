package cases

import Test
import backup.Backup
import backup.RestorePoint
import backup.clean.LargerThan
import data.SimulatedFileSystem
import storage.FileStorageProvider

object TestUseCase2 : Test("Use case 2") {

    override val path = "resources-bak/test-2"

    private val storage = FileStorageProvider(path)
    private val fileSystem = SimulatedFileSystem

    override fun run() {
        super.run()
        val backup = Backup(storage, fileSystem)

        backup.addPath("a/b/c")
        backup.addPath("a/b/x")
        backup.createRestorePoint(RestorePoint.Type.Complete)
        backup.createRestorePoint(RestorePoint.Type.Complete)

        require(backup.totalSize == 400L shl 20) { "Expected total size to be 400 Mb" }
        println("Ok, got [${backup.totalSize shr 20}] Mb total size")

        backup.clean(LargerThan(150))
        require(backup.restorePointsCount == 1) { "Expected only one restore point to remain" }
        println("Ok, got [${backup.restorePointsCount}] restore points in backup")
    }

}