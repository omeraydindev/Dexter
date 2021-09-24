package ma.dexter.tools.decompilers

import ma.dexter.tools.decompilers.cfr.CFRDecompiler
import ma.dexter.tools.decompilers.fernflower.FernflowerDecompiler
import ma.dexter.tools.decompilers.jdcore.JDCoreDecompiler
import ma.dexter.tools.decompilers.procyon.ProcyonDecompiler
import java.io.File

/**
 * [CFRDecompiler], [FernflowerDecompiler], [JDCoreDecompiler], [ProcyonDecompiler].
 */
interface BaseJarDecompiler: BaseDecompiler {

    fun decompileJar(className: String, jarFile: File): String

}
