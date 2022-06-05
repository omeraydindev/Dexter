package ma.dexter.tools.d2j

import com.googlecode.d2j.dex.Dex2jar
import ma.dexter.tools.jar.JarTool
import java.io.File

class D2JInvoker(
    private val dexFile: File,
    private val outJar: File,
    private val options: D2JOptions = D2JOptions(),
    private val progressCallback: (String) -> Unit = {},
) {
    /**
     * Runs [Dex2jar] on given [dexFile] and outputs to [outJar].
     */
    fun invoke(): Result {
        val handler = D2JExceptionHandler()

        /** create out directory with the same name as [outJar] but "_" appended */
        val outDir = File(outJar.parent, outJar.name + "_").also { it.mkdirs() }

        // invoke dex2jar
        invokeInternal(outDir, handler)

        // create the Jar
        progressCallback("Writing to JAR")
        JarTool(outDir, outJar).create()

        // clean up the temp dir
        progressCallback("Cleaning up temp files")
        outDir.deleteRecursively()

        return Result(
            success = !handler.hasException(),
            error = handler.getExceptions()
        )
    }

    /**
     * Adapted from [com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine].
     */
    private fun invokeInternal(
        outPath: File,
        handler: D2JExceptionHandler,
    ) {
        D2JFacade.from(dexFile)
            .withExceptionHandler(handler)
            .reUseReg(options.reuseReg)
            .topoLogicalSort(options.topoLogicalSort)
            .skipDebug(!options.debugInfo)
            .optimizeSynchronized(options.optimizeSynchronized)
            .printIR(options.printIR)
            .noCode(options.skipMethodBodies)
            .skipExceptions(options.skipExceptions)
            .currentClassCallback {
                progressCallback("Processing: $it")
            }
            .to(outPath.toPath()) // Careful!
    }

    class Result(val success: Boolean, val error: String)
}
