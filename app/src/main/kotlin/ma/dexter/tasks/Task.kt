package ma.dexter.tasks

import android.app.ProgressDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
) {
    companion object {
        fun <T> fromException(e: Throwable) =
            Result<T>(
                success = false,
                error = Error("Exception", Log.getStackTraceString(e))
            )
    }
}

class Error(
    val title: String = "",
    val message: String = ""
)

fun <T> ITask<T>.runWithDialog(
    context: Context,
    title: String,
    message: String,
    showDialogOnError: Boolean = true,
    callback: (Result<T>) -> Unit
) {
    val dialog = ProgressDialog.show(context, title, message, true, false)
    val handler = Handler(Looper.getMainLooper())

    Executors.newSingleThreadExecutor().execute {
        val result = try {
            when (this) {
                is ProgressTask -> run {
                    handler.post {
                        dialog.setMessage(it)
                    }
                }
                is Task -> run()
            }
        } catch (e: Throwable) {
            Result.fromException(e)
        }

        handler.post {
            dialog.dismiss()

            if (showDialogOnError && !result.success) {
                MaterialAlertDialogBuilder(context)
                    .setTitle("Error: " + result.error.title)
                    .setMessage(result.error.message)
                    .show()
            }
            callback(result)
        }
    }
}
