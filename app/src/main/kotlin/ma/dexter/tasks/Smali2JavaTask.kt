package ma.dexter.tasks

import ma.dexter.App
import ma.dexter.dex.MutableClassDef
import ma.dexter.dex.MutableDexFile
import ma.dexter.tools.d2j.D2JInvoker
import ma.dexter.tools.decompilers.BaseDecompiler
import ma.dexter.tools.decompilers.BaseDecompiler.Companion.decompile
import ma.dexter.tools.decompilers.BaseDexDecompiler
import ma.dexter.tools.decompilers.BaseJarDecompiler
import java.io.File

class Smali2JavaTask(
    private val classDefs: List<MutableClassDef>,
    private val className: String,
    private val decompiler: BaseDecompiler
) : ProgressTask<String>() {

    override fun run(
        progress: (String) -> Unit
    ): Result<String> {

        // Clear out cache
        val parent = App.context.cacheDir.apply {
            deleteRecursively()
            mkdirs()
        }

        // Initialize temp files
        val dexFile = File(parent, "out.dex")
        val jarFile = File(parent, "out.jar")

        // Initialize temp vars
        val isJarDecompiler = decompiler is BaseJarDecompiler

        // Assemble ClassDefs to DEX
        progress("Assembling DEX...")
        MutableDexFile(classDefs).writeToFile(dexFile)

        /**
         * if the decompiler is not a [BaseJarDecompiler] (which could only mean
         * it's JADX, a [BaseDexDecompiler]) we don't need to run d2j since
         * JADX operates on dex anyway.
         */
        if (isJarDecompiler) {
            // Invoke dex2jar
            progress("Converting DEX to JAR...")
            val d2jResult = D2JInvoker(listOf(dexFile), jarFile).invoke()

            if (!d2jResult.success) {
                return Result.failure("Dex2Jar", d2jResult.error)
            }
        }

        // Invoke the decompiler
        progress("Decompiling ${if (isJarDecompiler) "JAR" else "DEX"} to Java...")

        if (isJarDecompiler && !jarFile.exists()) {
            return Result.failure(
                decompiler.getName(),
                "Couldn't find generated JAR in ${jarFile.absolutePath}"
            )
        }

        val javaCode = try {

            decompiler.decompile(
                className,
                if (isJarDecompiler) jarFile else dexFile
            )

        } catch (e: Throwable) {
            return Result.failure(
                decompiler.getName(),
                "Couldn't decompile $className, logs:\n\n${e.stackTraceToString()}"
            )
        }

        return Result.success(javaCode)
    }

}
