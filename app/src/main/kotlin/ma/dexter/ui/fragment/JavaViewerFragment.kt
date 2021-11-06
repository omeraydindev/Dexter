package ma.dexter.ui.fragment

import android.os.Bundle
import android.view.View
import io.github.rosemoe.sora.langs.java.JavaLanguage
import ma.dexter.ui.editor.scheme.smali.SchemeLightSmali
import ma.dexter.model.JavaGotoDef

class JavaViewerFragment(
    private val javaGotoDef: JavaGotoDef
) : BaseCodeEditorFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(codeEditor) {
            colorScheme = SchemeLightSmali()
            isEditable = false

            setText(javaGotoDef.javaCode)
            setEditorLanguage(JavaLanguage())
        }
    }

}
