package ma.dexter.ui.util

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import io.github.rosemoe.sora.interfaces.EditorEventListener
import io.github.rosemoe.sora.text.Cursor
import io.github.rosemoe.sora.widget.CodeEditor
import ma.dexter.App
import ma.dexter.editor.util.SingleEditorEventListener

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
        bottom ?: params.bottomMargin
    )
    layoutParams = params
}
