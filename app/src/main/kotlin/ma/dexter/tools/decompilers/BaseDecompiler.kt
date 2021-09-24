package ma.dexter.tools.decompilers

import ma.dexter.tools.decompilers.cfr.CFRDecompiler
import ma.dexter.tools.decompilers.fernflower.FernflowerDecompiler
import ma.dexter.tools.decompilers.jadx.JADXDecompiler
import ma.dexter.tools.decompilers.jdcore.JDCoreDecompiler
import ma.dexter.tools.decompilers.procyon.ProcyonDecompiler
import java.io.File

sealed interface BaseDecompiler {

    fun getBanner(): String?

    fun getName(): String

    companion object {

        /**
         * Decompiles given [dexOrJarFile] and returns the decompiled Java code.
         */
        fun BaseDecompiler.decompile(className: String, dexOrJarFile: File): String {
            return when (this) {
                is BaseJarDecompiler -> decompileJar(className, jarFile = dexOrJarFile)
                is BaseDexDecompiler -> decompileDex(className, dexFile = dexOrJarFile)
            }
        }

        /**
         * Returns an immutable list of available decompilers.
         */
        fun getDecompilers() = listOf(
            JADXDecompiler(),
            JADXDecompiler(fallbackMode = true),
            FernflowerDecompiler(),
            CFRDecompiler(),
            JDCoreDecompiler(),
            ProcyonDecompiler()
        )

    }
}
