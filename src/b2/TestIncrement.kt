package cases

import Test
import backup.Backup
import backup.RestorePoint
import backup.clean.LargerThan
import backup.clean.LongerThan
import backup.clean.OlderThan
import data.LocalFileSystem
import data.SimulatedFileSystem
import storage.ArchiveStorageProvider
import java.time.Instant

object TestIncrement : Test("Increment") {

    override val path = "resources-bak/test-3"

    private val storage = ArchiveStorageProvider(path)
    private val fileSystem = SimulatedFileSystem
    private val testFileSystem = LocalFileSystem(path)

    override fun run() {
        super.run()
        val backup = Backup(storage, fileSystem)

        backup.addPath("b/a/c/y.mp3")
        backup.createRestorePoint(RestorePoint.Type.Complete)

        backup.addPath("a/b/c")
        backup.createRestorePoint(RestorePoint.Type.Incremental)

        backup.addPath("a/x.txt")
        backup.createRestorePoint(RestorePoint.Type.Complete)

        backup.createRestorePoint(RestorePoint.Type.Incremental)

        backup.addPath("a/b/x")
        backup.createRestorePoint(RestorePoint.Type.Incremental)

        check(listOf(1, 2, 3, 1, 2))

        backup.clean(LongerThan(5) and OlderThan(Instant.now()))
        check(listOf(1, 2, 3, 1, 2))

        backup.clean(LongerThan(5) or LargerThan(0))
        check(listOf(3, 1, 2))
    }

    private fun check(countAssertions: List<Int>) {
        val files = testFileSystem.allFiles("")
        require(files.size == countAssertions.size) {
            "Expected ${countAssertions.size} restore points, got ${files.size}"
        }
        files.sortedBy { it.path }.forEachIndexed { index, fileInfo ->
            val lines = testFileSystem.actualFile(fileInfo.path).readLines().size
            require(lines == countAssertions[index]) {
                "Expected ${countAssertions[index]} files in restore point number ${index}, got $lines"
            }
        }
        println("Ok, check with $countAssertions passed")
    }

}