package ma.dexter.tasks

import ma.dexter.project.DexProject
import ma.dexter.util.normalizeSmaliPath
import org.jf.dexlib2.writer.io.FileDataStore
import org.jf.dexlib2.writer.pool.DexPool

class SaveDexTask : ProgressTask<Nothing>() {

    // todo: move to [MutableDexContainer]
    override fun run(
        progress: (String) -> Unit
    ): Result<Nothing> {
        val dexEntries = DexProject.getOpenedProject().dexContainer.entries

        dexEntries.forEach { dexEntry ->
            val dexPool = DexPool(dexEntry.opcodes)
            val dexFile = dexEntry.dexFile!!

            dexEntry.classes.forEach { classDefEntry ->
                progress("Writing: ${normalizeSmaliPath(classDefEntry.type)} in ${dexFile.name}")
                dexPool.internClass(classDefEntry.classDef)
            }

            dexPool.writeTo(FileDataStore(dexFile))
        }

        return Result(success = true)
    }

}
