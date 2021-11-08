package ma.dexter.dex

import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import io.github.rosemoe.sora.widget.CodeEditor
import ma.dexter.model.JavaGotoDef
import ma.dexter.model.SmaliGotoDef
import ma.dexter.project.Workspace
import ma.dexter.ui.model.JavaItem
import ma.dexter.ui.model.SmaliItem
import ma.dexter.ui.viewmodel.MainViewModel
import ma.dexter.util.FIELD_METHOD_CALL_REGEX
import ma.dexter.util.toast
import ma.dexter.util.tokenizeSmali
import org.jf.smali.smaliParser

class DexGotoManager(
    fragmentActivity: FragmentActivity
) {
    private val viewModel: MainViewModel by fragmentActivity.viewModels()

    /**
     * Goes to the class/member definition that the cursor is at, of the given [CodeEditor].
     * (The text doesn't necessarily have to be selected)
     *
     * Selected text will be stretched for more flexibility, for example
     * consider the following smali line:
     *
     * `sput-object v0, LsomePackage/someClass;->someField:I`
     *
     * If `somePackage` is selected, it will be stretched from both sides
     * to match `LsomePackage/someClass;` and will redirect to that class.
     *
     * If `someField` is selected, it will be stretched from both sides
     * to match `LsomePackage/someClass;->someField:I` and will redirect
     * to that field definition.
     */
    fun gotoDef(
        editor: CodeEditor
    ) {
        val cursor = editor.cursor

        // look for class defs
        tokenizeSmali(editor.text.toString()) { token, line, startColumn ->
            if (token.type == smaliParser.CLASS_DESCRIPTOR && line == cursor.leftLine) {
                val endColumn = startColumn + token.text.length

                if (cursor.leftColumn in startColumn..endColumn &&
                    cursor.rightColumn in startColumn..endColumn
                ) {
                    editor.setSelectionRegion(
                        cursor.leftLine, startColumn,
                        cursor.rightLine, endColumn
                    )

                    gotoClassDef(token.text)
                    return
                }
            }
        }

        // look for field/method calls (on a line-by-line basis)
        editor.text.toString().lines().forEachIndexed { lineNumber, line ->
            FIELD_METHOD_CALL_REGEX.matchEntire(line)?.let {
                val range = it.groups[1]!!.range

                if (cursor.leftLine == lineNumber &&
                    cursor.leftColumn in range
                ) {
                    editor.setSelectionRegion(
                        cursor.leftLine, range.first,
                        cursor.rightLine, range.last + 1 // to make it exclusive
                    )

                    val (_, definingClass, descriptor) = it.destructured
                    gotoClassDef(definingClass, descriptor)
                    return
                }
            }
        }
    }

    fun gotoClassDef(
        dexClassDef: String,
        memberDescriptorToGo: String? = null
    ) {
        Workspace.getOpenedProject()
            .dexContainer.findClassDef(dexClassDef)?.let {
                gotoClassDef(SmaliGotoDef(it, memberDescriptorToGo))
                return
            }

        toast("Couldn't find class def: $dexClassDef")
    }

    fun gotoClassDef(
        smaliGotoDef: SmaliGotoDef
    ) {
        viewModel.gotoPageItem(SmaliItem(smaliGotoDef))
    }

    fun gotoJavaViewer(
        javaGotoDef: JavaGotoDef
    ) {
        viewModel.gotoPageItem(JavaItem(javaGotoDef))
    }

}
