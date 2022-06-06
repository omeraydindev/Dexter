package ma.dexter.tasks

import ma.dexter.tools.smali.BaksmaliInvoker
import java.io.File
import kotlin.system.measureTimeMillis

// TODO: execute in parallel to fasten the process
class BaksmaliDexTask(
    private val dexFile: File,
    private val zipFile: File,
): ProgressTask<String>() {

    override fun run(progress: (String) -> Unit): Result<String> {
        val invoker = BaksmaliInvoker()

        val time = measureTimeMillis {
            invoker.disassemble(
                dexFile,
                zipFile,
            ) { className, current, total ->
                progress("[$current/$total]\n$className")
            }
        }

        return Result.success("""
            Took: $time ms
            
            Saved to ${zipFile.absolutePath}""".trimIndent())
    }

}
