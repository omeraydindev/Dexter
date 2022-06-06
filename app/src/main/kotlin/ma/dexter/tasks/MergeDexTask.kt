package ma.dexter.tasks

import ma.dexter.util.normalizeSmaliPath
import org.jf.dexlib2.DexFileFactory
import org.jf.dexlib2.Opcodes
import org.jf.dexlib2.writer.io.FileDataStore
import org.jf.dexlib2.writer.pool.DexPool
import java.io.File
import kotlin.system.measureTimeMillis

// TODO: execute in parallel to fasten the process
class MergeDexTask(
    private val dexPaths: Array<String>,
    private val mergedDexFile: File
) : ProgressTask<String>() {

    override fun run(progress: (String) -> Unit): Result<String> {
        val dexPool = DexPool(Opcodes.forApi(31))

        var totalTime = 0L
        val statistics = StringBuilder()

        dexPaths.forEach { dexPath ->
            val dex = DexFileFactory.loadDexFile(dexPath, null)
            statistics.append("${File(dexPath).name}:\n")

            val interningTime = measureTimeMillis {
                dex.classes.forEach { classDef ->
                    progress("Interning: ${normalizeSmaliPath(classDef.type)}")
                    dexPool.internClass(classDef)
                }
            }

            totalTime += interningTime
            statistics.append("  Interning: $interningTime ms\n\n")
        }

        val writingTime = measureTimeMillis {
            progress("Writing to ${mergedDexFile.name}")
            dexPool.writeTo(FileDataStore(mergedDexFile))
        }
        totalTime += writingTime

        statistics.append("Writing: $writingTime ms\n")
        statistics.append("TOTAL: $totalTime ms")

        return Result.success(statistics.toString())
    }

}
