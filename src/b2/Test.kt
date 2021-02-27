import java.io.File

abstract class Test(private val id: String) {

    protected abstract val path: String

    open fun run() {
        cleanup()
        println("== Running test '$id' ==")
    }

    protected open fun cleanup() {
        File(path).deleteRecursively()
    }

}