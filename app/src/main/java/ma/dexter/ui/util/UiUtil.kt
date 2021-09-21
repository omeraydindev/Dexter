package ma.dexter.ui.util

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import io.github.rosemoe.sora.widget.CodeEditor
import ma.dexter.App

val Int.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        App.context.resources.displayMetrics
    ).toInt()

fun View.setMargins(left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null) {
    val params = (layoutParams as? ViewGroup.MarginLayoutParams)
    params?.setMargins(
        left ?: params.leftMargin,
        top ?: params.topMargin,
        right ?: params.rightMargin,
        bottom ?: params.bottomMargin)
    layoutParams = params
}

fun CodeEditor.setDefaults() {
    isOverScrollEnabled = false
    inputType = EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS or EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE or EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
    importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO

    setTextSize(16f)
}

fun CodeEditor.getSelectedText(): String {
    val content = text.subContent(cursor.leftLine, cursor.leftColumn, cursor.rightLine, cursor.rightColumn)
    return content.toString()
}
