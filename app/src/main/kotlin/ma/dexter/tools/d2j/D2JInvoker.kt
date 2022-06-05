package ma.dexter.tools.d2j

import com.googlecode.d2j.dex.Dex2jar
import com.googlecode.d2j.reader.MultiDexFileReader
import java.io.File
import java.nio.file.Files

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
        val dexFileReader = MultiDexFileReader.open(Files.readAllBytes(dexFile.toPath()))

        val d2JFacade = D2JFacade(
            reader = dexFileReader,
            options = options,
            exceptionHandler = handler,
            progressConsumer = { className, current, total ->
                progressCallback("[$current/$total] Processing: $className")
            }
        )

        d2JFacade.convert(outJar)

        return Result(
            success = !handler.hasException(),
            error = handler.getExceptions()
        )
    }

    class Result(val success: Boolean, val error: String)
}
