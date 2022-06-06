package ma.dexter.tasks

import ma.dexter.project.Workspace
import ma.dexter.util.normalizeSmaliPath
import org.jf.dexlib2.writer.io.FileDataStore
import org.jf.dexlib2.writer.pool.DexPool
import kotlin.system.measureTimeMillis

//TODO: execute in parallel to fasten the process
class SaveDexTask : ProgressTask<String>() {

    // todo: move to [MutableDexContainer]
    override fun run(
        progress: (String) -> Unit
    ): Result<String> {
        val dexEntries = Workspace.getOpenedProject()
            .dexContainer.entries

        var totalTime = 0L
        val statistics = StringBuilder()

        dexEntries.forEach { dexEntry ->
            val dexPool = DexPool(dexEntry.opcodes)
            val dexFile = dexEntry.dexFile!!
            statistics.append("${dexFile.name}:\n")

            val interningTime = measureTimeMillis {
                dexEntry.classes.forEach { classDefEntry ->
                    progress("Interning: ${normalizeSmaliPath(classDefEntry.type)}")
                    dexPool.internClass(classDefEntry.classDef)
                }
            }

            val writingTime = measureTimeMillis {
                progress("Writing to ${dexFile.name}")
                dexPool.writeTo(FileDataStore(dexFile))
            }

            totalTime += interningTime + writingTime
            statistics.append("  Interning: $interningTime ms\n")
            statistics.append("  Writing: $writingTime ms\n\n")
        }

        statistics.append("TOTAL: $totalTime ms")

        return Result.success(statistics.toString())
    }

}
