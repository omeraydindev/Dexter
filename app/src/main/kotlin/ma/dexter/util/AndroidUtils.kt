package ma.dexter.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import ma.dexter.App

// to later remove them easily
fun debugToast(
    message: String
) {
    toast(message)
}

fun toast(
    message: String,
    duration: Int = Toast.LENGTH_SHORT
) {
    Toast.makeText(App.context, message, duration).show()
}

fun openUrl(
    context: Context,
    url: String
) {
    context.startActivity(
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        )
    )
}
