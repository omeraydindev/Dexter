package ma.dexter.tasks

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ma.dexter.ui.dialog.ProgressDialog
import java.util.concurrent.Executors

fun <T> ITask<T>.runWithDialog(
    context: Context,
    title: String,
    message: String,
    showDialogOnError: Boolean = true,
    callback: (Result<T>) -> Unit
) {
    val dialog = ProgressDialog(context, title, message).show()
    val handler = Handler(Looper.getMainLooper())

    Executors.newSingleThreadExecutor().execute {
        val result = try {
            when (this) {
                is ProgressTask -> run {
                    handler.post { dialog.setMessage(it) }
                }
                is Task -> run()
            }
        } catch (e: Throwable) {
            Result.failure("Exception", e.stackTraceToString())
        }

        handler.post {
            dialog.dismiss()

            if (showDialogOnError && !result.success) {
                MaterialAlertDialogBuilder(context)
                    .setTitle("Error: " + result.error.title)
                    .setMessage(result.error.message.trim())
                    .show()
            }
            callback(result)
        }
    }
}
