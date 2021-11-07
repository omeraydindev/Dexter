package ma.dexter.util

import java.io.OutputStream
import java.io.PrintStream
import java.io.PrintWriter
import java.io.StringWriter
import java.net.UnknownHostException

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

/**
 * Copied from `Log.getStackTraceString`
 * (to not depend on Android-specific classes as much as possible.)
 */
fun getStackTraceString(tr: Throwable?): String {
    if (tr == null) {
        return ""
    }

    var t: Throwable? = tr
    while (t != null) {
        if (t is UnknownHostException) {
            return ""
        }
        t = t.cause
    }

    val sw = StringWriter()
    val pw = PrintWriter(sw)
    tr.printStackTrace(pw)
    pw.flush()
    return sw.toString()
}
