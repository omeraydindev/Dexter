package ma.dexter.tools.d2j

import android.util.Log
import com.googlecode.d2j.dex.Dex2jar
import java.io.File
import java.lang.Exception

object D2JInvoker {

    /**
     * Runs [Dex2jar] on given [dexFile] and outputs to [outPath].
     * Doesn't support outputting to a jar for now, due to FileSystem limitations.
     *
     * Adapted from [com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine].
     *
     * @param outPath Output directory (must exist)
     */
    fun invoke(
        dexFile: File,
        outPath: File,
        options: D2JOptions = D2JOptions()
    ): Result {
        val handler = D2JExceptionHandler()

        try {
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

        } catch (e: Exception) { // catch everything, will be shown to user anyway
            return Result(false, Log.getStackTraceString(e))
        }

        return Result(!handler.hasException(), handler.getExceptions())
    }

    class Result(val success: Boolean, val error: String)

}
