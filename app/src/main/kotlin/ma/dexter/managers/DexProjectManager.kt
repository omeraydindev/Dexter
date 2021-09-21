package ma.dexter.managers

import android.content.Context
import android.content.Intent
import android.widget.Toast
import io.github.rosemoe.sora.widget.CodeEditor
import ma.dexter.model.tree.DexClassItem
import ma.dexter.core.DexBackedDex
import ma.dexter.tools.smali.BaksmaliInvoker
import ma.dexter.ui.activity.code.JavaViewerActivity
import ma.dexter.ui.activity.code.SmaliEditorActivity
import ma.dexter.util.Releasable
import ma.dexter.util.tokenizeSmali
import org.jf.dexlib2.dexbacked.DexBackedClassDef
import org.jf.smali.smaliParser

object DexProjectManager {
    var dexList: List<DexBackedDex>? = null

    /**
     * String being the smali code.
     */
    private var smalis = mutableMapOf<DexClassItem, String>()

    // their values will be set in this class only, hence the "private set"s
    var currentDexClassDef = Releasable<DexBackedClassDef>()
        private set
    var currentJavaCode = Releasable<String>()
        private set

    /**
     * Goes to the class definition that the cursor is at, of the given [CodeEditor].
     * (The text doesn't necessarily have to be selected)
     *
     * Selected text will be stretched for more flexibility, for example
     * consider the following smali line:
     *
     * >sput-object v0, LsomePackage/someClass;->a:I
     *
     * If "somePackage" is selected, it will be stretched from both sides
     * to match "LsomePackage/someClass;" and will redirect to that class.
     */
    fun gotoDef(context: Context, editor: CodeEditor) {
        val cursor = editor.cursor

        // look for class defs
        tokenizeSmali(editor.text.toString()) { token, line, startColumn ->

            if (token.type == smaliParser.CLASS_DESCRIPTOR && line == cursor.leftLine) {
                val endColumn = startColumn + token.text.length

                if (cursor.leftColumn in startColumn..endColumn) {
                    editor.setSelectionRegion(
                        cursor.leftLine, startColumn,
                        cursor.rightLine, endColumn
                    )

                    gotoClassDef(context, token.text)
                }
            }
        }

        // TODO: look for method/field invocations/calls
    }

    @SuppressWarnings
    fun gotoClassDef(context: Context, dexClassDef: String) {
        dexList?.forEach { dex ->
            dex.findClassDefByDescriptor(dexClassDef)?.let {
                gotoClassDef(context, it)
                return
            }
        }

        Toast.makeText(context, "Couldn't find class def: $dexClassDef", Toast.LENGTH_SHORT).show()
    }

    fun gotoClassDef(context: Context, dexClassDef: DexBackedClassDef) {
        currentDexClassDef.value = dexClassDef

        context.startActivity(
            Intent(context, SmaliEditorActivity::class.java)
        )
    }

    fun gotoJavaViewer(context: Context, javaCode: String) {
        currentJavaCode.value = javaCode

        context.startActivity(
            Intent(context, JavaViewerActivity::class.java)
        )
    }

    // Smali

    fun getSmali(dexClassItem: DexClassItem): String {
        if (dexClassItem in smalis) {
            return smalis[dexClassItem]!!
        }

        smalis[dexClassItem] = BaksmaliInvoker.disassemble(dexClassItem.dexClassDef)
        return getSmali(dexClassItem)
    }

}
