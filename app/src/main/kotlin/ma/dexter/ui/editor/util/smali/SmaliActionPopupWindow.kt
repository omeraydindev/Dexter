package ma.dexter.ui.editor.util.smali

import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.material.button.MaterialButton
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.TextActionPopupWindow
import ma.dexter.R
import ma.dexter.dex.DexGotoManager

class SmaliActionPopupWindow(
    private val fragmentActivity: FragmentActivity,
    private val editor: CodeEditor
) : TextActionPopupWindow(editor) {

    init {

        val moreButton = contentView.findViewById<MaterialButton>(
            io.github.rosemoe.sora.R.id.tcpw_material_button_save_to_project
        )

        moreButton.run {
            text = context.getString(R.string.smali_goto)
            icon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_arrow_right_alt_20)

            setOnClickListener {
                DexGotoManager(fragmentActivity)
                    .gotoDef(editor)
            }
        }

    }

}
