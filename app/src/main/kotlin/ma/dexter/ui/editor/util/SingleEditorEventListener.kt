package ma.dexter.ui.editor.util

import io.github.rosemoe.sora.interfaces.EditorEventListener
import io.github.rosemoe.sora.text.Cursor
import io.github.rosemoe.sora.widget.CodeEditor

open class SingleEditorEventListener: EditorEventListener {
    override fun onRequestFormat(editor: CodeEditor) = false

    override fun onFormatFail(editor: CodeEditor, cause: Throwable?) = false

    override fun onFormatSucceed(editor: CodeEditor) {}

    override fun onNewTextSet(editor: CodeEditor) {}

    override fun afterDelete(
        editor: CodeEditor,
        content: CharSequence,
        startLine: Int,
        startColumn: Int,
        endLine: Int,
        endColumn: Int,
        deletedContent: CharSequence?
    ) {}

    override fun afterInsert(
        editor: CodeEditor,
        content: CharSequence,
        startLine: Int,
        startColumn: Int,
        endLine: Int,
        endColumn: Int,
        insertedContent: CharSequence?
    ) {}

    override fun beforeReplace(editor: CodeEditor, content: CharSequence) {}

    override fun onSelectionChanged(editor: CodeEditor, cursor: Cursor) {}
}
