package ma.dexter.tools.decompilers

import ma.dexter.tools.decompilers.jadx.JADXDecompiler
import java.io.File

/**
 * Currently only [JADXDecompiler].
 */
interface BaseDexDecompiler: BaseDecompiler {

    fun decompileDex(className: String, dexFile: File): String

}
