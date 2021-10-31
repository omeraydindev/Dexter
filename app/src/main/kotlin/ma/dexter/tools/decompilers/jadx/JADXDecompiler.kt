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
                if (it.fullName == className.replace("/", ".")) {
                    return getBanner() + it.code
                }
            }

            if (jadx.classes.size > 0) {
                return getBanner() + jadx.classes.first().code
            }

            return "// Error: JADX couldn't decompile $className"
        }

    }

    override fun getBanner() = """
            /*
             * Decompiled with JADX v1.2.0.
             */
        """.trimIndent() + "\n"

    override fun getName() = if (fallbackMode) "JADX (Fallback)" else "JADX"
}
