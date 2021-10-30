package ma.dexter.tools.smali.catcherr

import org.antlr.runtime.CommonTokenStream
import org.antlr.runtime.RecognitionException
import org.jf.smali.smaliParser

/**
 * A sub class of [smaliParser] that catches errors
 * and provides methods to retrieve them.
 *
 * Doesn't start with an uppercase letter to conform with [smaliParser]'s name.
 */
class smaliCatchErrParser(
    tokens: CommonTokenStream
) : smaliParser(tokens) {

    private val errors = StringBuilder()
    private val syntaxErrors = mutableListOf<SyntaxError>()

    override fun emitErrorMessage(msg: String?) {
        errors.append(msg)
        errors.append("\n")
    }

    fun getErrorsString() = errors.toString()

    override fun displayRecognitionError(tokenNames: Array<out String>?, e: RecognitionException) {

        syntaxErrors += SyntaxError(
            startLine = e.line,
            startColumn = e.charPositionInLine,
            endLine = e.line,
            endColumn = e.charPositionInLine + (e.token?.text?.length ?: 1),
            getErrorMessage(e, tokenNames)
        )

        super.displayRecognitionError(tokenNames, e)
    }

    fun getErrors(): List<SyntaxError> = syntaxErrors

}
