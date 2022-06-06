package ma.dexter.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import java.io.File

val storagePath: File
    @Suppress("DEPRECATION")
    get() = Environment.getExternalStorageDirectory()

fun requestAllFilesAccessPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()) {
        Intent().run {
            action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
            data = Uri.parse("package:${context.packageName}")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK

            try {
                context.startActivity(this)
            } catch (ignored: ActivityNotFoundException) {}
        }
    }
}

/**
 * Get [File] with a unique name, based on [baseName] and
 * [extension]. Makes the File unique by the Windows way, that is:
 *
 * If "[baseName].[extension]" doesn't exist, just returns that.
 * Otherwise, returns "[baseName] (N).[extension]" where **N** is
 * some number >= 2.
 */
fun getFileWithUniqueName(
    baseName: String,
    extension: String,
    directory: File,
): File {
    var count = 1
    while (true) {
        val file = File(
            directory,
            baseName + (if (count > 1) " ($count)" else "") + "." + extension
        )

        if (file.exists()) {
            count++
        } else {
            return file
        }
    }
}
