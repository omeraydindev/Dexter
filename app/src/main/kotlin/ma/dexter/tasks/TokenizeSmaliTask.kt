package ma.dexter.tasks

import ma.dexter.tools.smali.catcherr.smaliCatchErrFlexLexer
import org.antlr.runtime.CommonTokenStream
import org.jf.smali.smaliParser
import org.jf.util.StringUtils
import java.io.StringReader
import java.lang.StringBuilder

/**
 * Task used to print tokens from a given smali code. TODO: remove
 */
object TokenizeSmaliTask {

    fun execute(
        smaliCode: String,
        callback: (tokens: String) -> Unit
    ) {
        val lexer = smaliCatchErrFlexLexer(StringReader(smaliCode), 31)

        val tokens = CommonTokenStream(lexer)
        tokens.fill()

        if (lexer.numberOfSyntaxErrors > 0) {
            callback(lexer.getErrors())
            return
        }

        val result = StringBuilder()

        for (i in 0 until tokens.size()) {
            val token = tokens[i]

            val tokenName = if (token.type == -1) {
                "EOF"
            } else {
                smaliParser.tokenNames[token.type]
            }

            result.append(tokenName)
            result.append(" ")
            result.append(token.type)
            result.append(" (\"")
            result.append(StringUtils.escapeString(token.text))
            result.append("\")\n\n")
        }

        callback(result.toString())
    }

}
