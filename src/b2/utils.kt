import java.io.File

fun joinPaths(vararg paths: String): String =
    paths.joinToString(File.separator)