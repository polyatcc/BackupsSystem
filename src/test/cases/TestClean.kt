package cases

import Test
import backup.Backup
import backup.RestorePoint
import backup.clean.LongerThan
import backup.clean.OlderThan
import data.LocalFileSystem
import storage.FileStorageProvider
import java.time.Instant

object TestClean : Test("Clean combinations") {

    override val path = "resources-bak/test-4"

    private val storage = FileStorageProvider(path)
    private val fileSystem = LocalFileSystem("resources")

    override fun run() {
        super.run()
        runOneOf()
        runBoth()
    }

    private fun runOneOf() {
        cleanup()
        println("-- Subtest 'one of' --")

        val backup = Backup(storage, fileSystem)
        backup.addPath("foo")
        backup.createRestorePoint(RestorePoint.Type.Complete)

        backup.addPath("bar")
        backup.createRestorePoint(RestorePoint.Type.Incremental)

        val time = Instant.now()
        backup.createRestorePoint(RestorePoint.Type.Complete)

        backup.clean(OlderThan(time) or LongerThan(4))
        require(backup.restorePointsCount == 1) { "Expected one last point in backup" }
        println("Ok, got [${backup.restorePointsCount}] restore points")
    }

    private fun runBoth() {
        cleanup()
        println("-- Subtest 'both' --")

        val backup = Backup(storage, fileSystem)
        backup.createRestorePoint(RestorePoint.Type.Complete)

        backup.addPath("root")
        backup.createRestorePoint(RestorePoint.Type.Complete)

        backup.addPath("foo")
        backup.createRestorePoint(RestorePoint.Type.Complete)

        val time = Instant.now()
        backup.addPath("bar")
        backup.createRestorePoint(RestorePoint.Type.Incremental)

        backup.clean(OlderThan(time) and LongerThan(3))
        require(backup.restorePointsCount == 3) { "Expected three last points in backup" }
        println("Ok, got [${backup.restorePointsCount}] restore points")
    }

}