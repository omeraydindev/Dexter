package ma.dexter.editor.util

import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.TextActionPopupWindow
import ma.dexter.R
import ma.dexter.managers.DexProjectManager

class SmaliActionPopupWindow(private val editor: CodeEditor) : TextActionPopupWindow(editor) {

    init {

        val moreButton = contentView.findViewById<MaterialButton>(
            io.github.rosemoe.sora.R.id.tcpw_material_button_more
        )

        moreButton.run {
            text = context.getString(R.string.smali_goto)
            icon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_arrow_right_alt_20)

            setOnClickListener {
                DexProjectManager.gotoDef(context, editor)
                hide(DISMISS)
            }
        }

    }

}
