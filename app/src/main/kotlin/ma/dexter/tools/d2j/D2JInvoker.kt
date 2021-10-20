package ma.dexter.tools.d2j

import android.util.Log
import com.googlecode.d2j.dex.Dex2jar
import ma.dexter.tools.jar.JarTool
import java.io.File

object D2JInvoker {

    /**
     * Runs [Dex2jar] on given [dexFile] and outputs to [outJar].
     *
     * @param outJar output .jar path
     */
    fun invoke(
        dexFile: File,
        outJar: File,
        options: D2JOptions = D2JOptions()
    ): Result {
        val handler = D2JExceptionHandler()

        try {
            /** create out directory with the same name as [outJar] but "_" appended */
            val outDir = File(outJar.parent, outJar.name + "_").also { it.mkdirs() }

            // invoke dex2jar
            _invoke(dexFile, outDir, options, handler)

            // create the Jar
            JarTool(outDir, outJar).create()

            // clean up the temp dir
            outDir.deleteRecursively()
        } catch (e: Exception) { // catch everything, will be shown to user anyway
            return Result(false, Log.getStackTraceString(e))
        }

        return Result(!handler.hasException(), handler.getExceptions())
    }

    /**
     * Adapted from [com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine].
     */
    private fun _invoke(
        dexFile: File,
        outPath: File,
        options: D2JOptions,
        handler: D2JExceptionHandler,
    ) {
        Dex2jar.from(dexFile)
            .withExceptionHandler(handler)
            .reUseReg(options.reuseReg)
            .topoLogicalSort()
            .skipDebug(!options.debugInfo)
            .optimizeSynchronized(options.optimizeSynchronized)
            .printIR(false)
            .noCode(options.skipMethodBodies)
            .skipExceptions(options.skipExceptions)
            .to(outPath.toPath()) // Careful!
    }

    class Result(val success: Boolean, val error: String)
}
