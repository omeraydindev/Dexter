package ma.dexter.tasks

import android.app.ProgressDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.concurrent.Executors

sealed interface ITask<T>

abstract class Task<T> : ITask<T> {
    abstract fun run(): Result<T>
}

abstract class ProgressTask<T> : ITask<T> {
    abstract fun run(progress: (String) -> Unit): Result<T>
}

class Result<T>(
    val value: T? = null,
    val success: Boolean,
    val error: Error = Error()
)

class Error(
    val title: String = "",
    val message: String = ""
) {
    companion object {
        fun fromException(e: Exception) = Error("Exception", Log.getStackTraceString(e))
    }
}

fun <T> ITask<T>.runWithDialog(
    context: Context,
    title: String,
    message: String,
    callback: (Result<T>) -> Unit
) {
    val dialog = ProgressDialog.show(context, title, message, true, false)
    val handler = Handler(Looper.getMainLooper())

    Executors.newSingleThreadExecutor().execute {
        val result = when (this) {
            is ProgressTask -> run {
                handler.post {
                    dialog.setMessage(it)
                }
            }
            is Task -> run()
        }

        handler.post {
            dialog.dismiss()

            callback(result)
        }
    }
}
