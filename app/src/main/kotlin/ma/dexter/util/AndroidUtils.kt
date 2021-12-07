package ma.dexter.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import ma.dexter.App

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

fun hideKeyboard(view: View?) {
    if (view == null) return

    try {
        val imm = view.context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    } catch (ignored: Exception) {}
}

fun copyToClipboard(text: String) {
    val clipboard = App.context
        .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("", text)
    clipboard.setPrimaryClip(clip)
}

fun copyToClipboard(text: String, showToast: Boolean) {
    copyToClipboard(text)

    if (showToast) toast("Copied \"$text\" to clipboard")
}
