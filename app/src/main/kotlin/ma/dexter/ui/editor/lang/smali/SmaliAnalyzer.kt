package ma.dexter.ui.editor.lang.smali

import io.github.rosemoe.sora.data.BlockLine
import io.github.rosemoe.sora.data.Span
import io.github.rosemoe.sora.interfaces.CodeAnalyzer
import io.github.rosemoe.sora.text.TextAnalyzeResult
import io.github.rosemoe.sora.text.TextAnalyzer
import io.github.rosemoe.sora.widget.EditorColorScheme
import ma.dexter.ui.editor.lang.smali.model.SmaliAutoCompleteModel
import ma.dexter.ui.editor.lang.smali.model.SmaliClassDesc
import ma.dexter.ui.editor.scheme.smali.SmaliBaseScheme
import ma.dexter.tools.smali.catcherr.smaliCatchErrFlexLexer
import ma.dexter.tools.smali.catcherr.smaliCatchErrParser
import org.antlr.runtime.CommonTokenStream
import org.antlr.runtime.Token
import org.jf.smali.smaliFlexLexer
import org.jf.smali.smaliParser
import java.io.StringReader

class SmaliAnalyzer : CodeAnalyzer {

    override fun analyze(
        content: CharSequence,
        colors: TextAnalyzeResult,
        delegate: TextAnalyzer.AnalyzeThread.Delegate
    ) {
        val text = if (content is StringBuilder) content else StringBuilder(content)

        // For completion TODO
        val classDescList = mutableSetOf<SmaliClassDesc>()

        // For drawing lines between '.method' and '.end method' directives
        var lastBlockline: BlockLine? = null

        // Set the apiLevel to 31 so that
        // most recent dex opcodes are also supported.
        val lexer = smaliFlexLexer(StringReader(text.toString()), 31)
        lexer.setSuppressErrors(true)

        var token: Token
        var lastLine = 1

        while (delegate.shouldAnalyze()) {
            token = lexer.nextToken()

            if (token == null || token.type == smaliParser.EOF) break
            if (token.type == smaliParser.WHITE_SPACE) continue

            val line = token.line - 1
            lastLine = line
            val column = token.charPositionInLine

            when (token.type) {

                in directives -> {
                    colors.addIfNeeded(line, column, SmaliBaseScheme.DIRECTIVE)

                    if (token.type == smaliParser.METHOD_DIRECTIVE) {
                        lastBlockline = colors.obtainNewBlock().apply {
                            startLine = line
                            startColumn = column
                        }
                    }

                    if (token.type == smaliParser.END_METHOD_DIRECTIVE) {
                        lastBlockline?.run {
                            endLine = line
                            endColumn = column

                            if (startLine != endLine) colors.addBlockLine(this)
                        }
                    }
                }

                smaliParser.ANNOTATION_VISIBILITY,
                smaliParser.ACCESS_SPEC,
                in instructions -> {
                    colors.addIfNeeded(line, column, SmaliBaseScheme.ACCESS_MODIFIER)
                }

                smaliParser.CLASS_DESCRIPTOR,
                smaliParser.ARRAY_TYPE_PREFIX,
                smaliParser.PRIMITIVE_TYPE,
                smaliParser.PARAM_LIST_OR_ID_PRIMITIVE_TYPE,
                smaliParser.VOID_TYPE -> {
                    colors.addIfNeeded(line, column, SmaliBaseScheme.CLASS_DESCRIPTOR)

                    if (token.type == smaliParser.CLASS_DESCRIPTOR) {
                        classDescList += SmaliClassDesc(token.text)
                    }
                }

                smaliParser.POSITIVE_INTEGER_LITERAL,
                smaliParser.NEGATIVE_INTEGER_LITERAL,
                smaliParser.INTEGER_LITERAL -> {
                    colors.addIfNeeded(line, column, SmaliBaseScheme.INT_LITERAL)
                }

                smaliParser.REGISTER -> {
                    colors.addIfNeeded(line, column, SmaliBaseScheme.REGISTER)
                }

                smaliParser.ARROW,
                smaliParser.MEMBER_NAME,
                smaliParser.SIMPLE_NAME -> {
                    colors.addIfNeeded(line, column, SmaliBaseScheme.SIMPLE_NAME)
                }

                smaliParser.STRING_LITERAL -> {
                    colors.addIfNeeded(line, column, SmaliBaseScheme.STRING_LITERAL)
                }

                smaliParser.LINE_COMMENT -> {
                    colors.addIfNeeded(line, column, SmaliBaseScheme.LINE_COMMENT)
                }

                else -> {
                    colors.addIfNeeded(line, column, EditorColorScheme.TEXT_NORMAL)
                }
            }
        }

        colors.extra = SmaliAutoCompleteModel(classDescList)
        colors.determine(lastLine)

        markSyntaxErrors(text.toString(), colors)
    }

    // todo: should we care about the errors produced by [org.jf.smali.smaliTreeWalker] ?
    private fun markSyntaxErrors(
        smaliCode: String,
        colors: TextAnalyzeResult
    ) {
        val lexer = smaliCatchErrFlexLexer(StringReader(smaliCode), 31)
        val parser = smaliCatchErrParser(CommonTokenStream(lexer))

        parser.smali_file()

        // todo: show error message somehow
        (lexer.getErrors() + parser.getErrors()).forEach {
            colors.markProblemRegion(
                Span.FLAG_ERROR,
                it.startLine - 1,
                it.startColumn,
                it.endLine - 1,
                it.endColumn
            )
        }
    }

    companion object {
        private val instructions = 44..94 // see smaliParser

        private val directives by lazy {  // see smaliParser
            buildList {
                smaliParser.tokenNames.forEachIndexed { index, s ->
                    if (s.endsWith("_DIRECTIVE")) {
                        add(index)
                    }
                }
            }
        }
    }
}
