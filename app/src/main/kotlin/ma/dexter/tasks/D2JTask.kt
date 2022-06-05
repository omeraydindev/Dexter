package ma.dexter.tasks

import ma.dexter.tools.d2j.D2JInvoker
import java.io.File
import kotlin.system.measureTimeMillis

class D2JTask(
    private val dexFiles: List<File>,
    private val jarFile: File,
): ProgressTask<String>() {

    override fun run(
        progress: (String) -> Unit
    ): Result<String> {

        val time = measureTimeMillis {
            progress("Initializing...")

            val d2j = D2JInvoker(dexFiles, jarFile) { currentProgress ->
                progress(currentProgress)
            }
            val d2jResult = d2j.invoke()

            if (!d2jResult.success) {
                return Result.failure("Dex2Jar", d2jResult.error)
            }
        }

        return Result.success("""
            Took: $time ms
            
            Saved to ${jarFile.absolutePath}""".trimIndent())
    }

}
