package ma.dexter.tools.decompilers.jadx

import jadx.api.JadxArgs
import jadx.api.JadxDecompiler
import ma.dexter.tools.decompilers.BaseDexDecompiler
import java.io.File

class JADXDecompiler(
    private val fallbackMode: Boolean = false
) : BaseDexDecompiler {

    override fun decompileDex(
        className: String,
        dexFile: File
    ): String {

        val jadxArgs = JadxArgs().apply {
            inputFiles.add(dexFile)
            isSkipResources = true
            isRespectBytecodeAccModifiers = true
            isShowInconsistentCode = true // to avoid errors (?)
            isFallbackMode = fallbackMode
        }

        JadxDecompiler(jadxArgs).also { jadx ->
            jadx.load()
            // jadx.save() // Saves decompiled classes to storage, we don't want that

            jadx.classes.forEach {
                if (it.fullName.replace(".", "/") == className) {
                    return getBanner() + it.code
                }
            }

            return "Error" // TODO (does this even ever happen?)
        }

    }

    override fun getBanner() = """
            /*
             * Decompiled with JADX v0.9.0.
             */
        """.trimIndent() + "\n"

    override fun getName() = if (fallbackMode) "JADX (Fallback)" else "JADX"
}
