package ma.dexter.tasks

import ma.dexter.tools.d2j.D2JInvoker
import java.io.File
import kotlin.system.measureTimeMillis

class D2JTask(
    private val dexFile: File,
    private val jarFile: File,
): ProgressTask<String>() {

    override fun run(
        progress: (String) -> Unit
    ): Result<String> {

        val time = measureTimeMillis {
            progress("Running D2J")

            val d2jResult = D2JInvoker.invoke(dexFile, jarFile)
            if (!d2jResult.success) {
                return Result.failure("Dex2Jar", d2jResult.error)
            }
        }

        return Result.success("""
            Took: $time ms
            
            Saved to ${jarFile.absolutePath}""".trimIndent())
    }

}
