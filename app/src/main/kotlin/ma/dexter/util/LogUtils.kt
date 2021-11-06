package ma.dexter.util

import java.io.OutputStream
import java.io.PrintStream

/**
 * Listens for System.out/err logs and invokes the callback
 * when received.
 *
 * @param errLogsEnabled Specifies whether it should listen for System.err logs as well
 * @param callback Callback to be invoked when a log is received
 */
fun listenForSystemLogs(
    errLogsEnabled: Boolean = true,
    callback: (String) -> Unit
) {
    val outStream = PrintStream(object : OutputStream() {
        private val cache = StringBuilder()

        override fun write(b: Int) {
            // print line by line
            if (b.toChar() == '\n') {
                callback(cache.toString())

                cache.clear()
            } else {
                cache.append(b.toChar())
            }
        }
    })

    System.setOut(outStream)
    if (errLogsEnabled) System.setErr(outStream)
}
