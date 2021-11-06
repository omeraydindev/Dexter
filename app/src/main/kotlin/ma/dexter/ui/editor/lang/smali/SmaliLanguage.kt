package ma.dexter.ui.editor.lang.smali

import io.github.rosemoe.sora.interfaces.EditorLanguage
import io.github.rosemoe.sora.interfaces.NewlineHandler
import io.github.rosemoe.sora.langs.internal.MyCharacter
import io.github.rosemoe.sora.widget.SymbolPairMatch

class SmaliLanguage: EditorLanguage {
    override fun getAnalyzer() = SmaliAnalyzer()

    override fun getAutoCompleteProvider() = SmaliAutoCompleteProvider()

    override fun isAutoCompleteChar(p0: Char): Boolean {
        return MyCharacter.isJavaIdentifierPart(p0.code)

                // for instructions like const-wide/high16
                || p0 in "0123456789-/"

                // for directives
                || p0 == '.'
    }

    override fun format(p0: CharSequence) = p0

    override fun getIndentAdvance(p0: String?) = 0

    override fun useTab() = true

    override fun getSymbolPairs() = SymbolPairMatch.DefaultSymbolPairs()

    override fun getNewlineHandlers() = arrayOf<NewlineHandler>()
}
