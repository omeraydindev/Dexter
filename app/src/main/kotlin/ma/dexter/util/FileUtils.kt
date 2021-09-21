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
