package ma.dexter.tasks

import android.os.Handler
import android.os.Looper
import ma.dexter.App
import ma.dexter.tools.d2j.D2JInvoker
import ma.dexter.tools.decompilers.BaseDecompiler
import ma.dexter.tools.decompilers.BaseDecompiler.Companion.decompile
import ma.dexter.tools.decompilers.BaseDexDecompiler
import ma.dexter.tools.decompilers.BaseJarDecompiler
import ma.dexter.tools.smali.SmaliInvoker
import java.io.File
import java.util.concurrent.Executors

// TODO: migrate to coroutines
// TODO: Make multiple files possible too (for anonymous/inner classes)
object Smali2JavaTask {

    fun execute(
        smaliCode: String,
        className: String,
        decompiler: BaseDecompiler,
        progress: (info: String) -> Unit = {},
        callback: (result: Result) -> Unit = {}
    ) {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            val invokeResult = execute_(smaliCode, className, decompiler) {
                handler.post { // run progress on UI thread
                    progress(it)
                }
            }

            handler.post {
                callback(invokeResult)
            }
        }
    }

    /*
     * Returns decompiled Java code.
     */
    private fun execute_(
        smaliCode: String,
        className: String,
        decompiler: BaseDecompiler,
        progress: (info: String) -> Unit
    ): Result {

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


        // Invoke smali
        progress("Assembling smali to dex...")
        val smaliResult = SmaliInvoker.assemble(smaliCode, dexFile)

        if (!smaliResult.success) {
            return Error(title = "Smali", message = smaliResult.error)
        }


        /**
         * if the decompiler is not a [BaseJarDecompiler] (which could only mean
         * it's JADX, a [BaseDexDecompiler]) we don't need to run d2j since
         * JADX operates on dex anyway.
         */
        if (isJarDecompiler) {

            // Invoke dex2jar
            progress("Converting dex to jar...")
            val d2jResult = D2JInvoker.invoke(dexFile, jarFile)

            if (!d2jResult.success) {
                return Error(title = "Dex2Jar", message = d2jResult.error)
            }

        }
        

        // Invoke the decompiler
        progress("Decompiling ${ if (isJarDecompiler) "jar" else "dex" } to Java...")

        val javaCode = if (jarFile.exists() || !isJarDecompiler) {

            decompiler.decompile(
                className,
                if (isJarDecompiler) jarFile else dexFile
            )

        } else {
            return Error(
                title = decompiler.getName(),
                message = "Couldn't find generated .jar in ${jarFile.absolutePath}"
            )
        }


        return Success(javaCode)
    }

    sealed interface Result

    class Success(
        val javaCode: String
    ): Result

    class Error(
        val title: String,
        val message: String
    ): Result

}
