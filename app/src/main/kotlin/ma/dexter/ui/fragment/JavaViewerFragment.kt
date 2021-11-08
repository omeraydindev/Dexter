package ma.dexter.ui.fragment

import android.os.Bundle
import android.view.View
import com.github.zawadz88.materialpopupmenu.MaterialPopupMenuBuilder
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.rosemoe.sora.langs.java.JavaLanguage
import ma.dexter.R
import ma.dexter.model.JavaGotoDef
import ma.dexter.parsers.java.JavaMember
import ma.dexter.parsers.java.parseJava
import ma.dexter.ui.editor.scheme.smali.SchemeLightSmali
import ma.dexter.util.hideKeyboard

class JavaViewerFragment(
    private val javaGotoDef: JavaGotoDef
) : BaseCodeEditorFragment() {

    // parse the Java code only once since it won't change
    private val javaFile by lazy {
        parseJava(codeEditor.text.toString())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(codeEditor) {
            colorScheme = SchemeLightSmali()
            isEditable = false

            setText(javaGotoDef.javaCode)
            setEditorLanguage(JavaLanguage())
        }
    }

    override fun beforeBuildMoreMenu(popupMenuBuilder: MaterialPopupMenuBuilder) {
        popupMenuBuilder.section {
            item {
                label = "Navigation"
                iconDrawable = drawable(R.drawable.ic_baseline_view_stream_24)
                callback = ::showNavigationDialog
            }
        }
    }

    private fun showNavigationDialog() {
        val navItems = javaFile.members

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Navigation")
            .setItems(navItems.map { it.name }.toTypedArray()) { _, pos ->
                gotoMemberDefinition(navItems[pos])
            }
            .show()
    }

    private fun gotoMemberDefinition(member: JavaMember) {
        hideKeyboard(codeEditor)

        codeEditor.jumpToLine(member.line)
    }

}
