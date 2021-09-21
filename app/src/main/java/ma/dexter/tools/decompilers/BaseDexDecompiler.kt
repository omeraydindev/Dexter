package ma.dexter.tools.decompilers

import java.io.File

/**
 * Currently only Jadx.
 */
interface BaseDexDecompiler: BaseDecompiler {

    fun decompileDex(dexFile: File): String

}
