package ma.dexter.tools.decompilers

import ma.dexter.tools.decompilers.cfr.CFRDecompiler
import ma.dexter.tools.decompilers.fernflower.FernflowerDecompiler
import ma.dexter.tools.decompilers.jadx.JADXDecompiler
import ma.dexter.tools.decompilers.jdcore.JDCoreDecompiler
import ma.dexter.tools.decompilers.procyon.ProcyonDecompiler

sealed interface BaseDecompiler {

    fun getBanner(): String?

    fun getName(): String

    companion object {

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
