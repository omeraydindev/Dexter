package ma.dexter.util

import android.widget.Toast
import ma.dexter.App

fun toast(
    message: String,
    duration: Int = Toast.LENGTH_SHORT
) {
    Toast.makeText(App.context, message, duration).show()
}
