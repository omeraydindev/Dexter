package ma.dexter.tools.smali.catcherr

import org.jf.smali.InvalidToken
import org.jf.smali.smaliFlexLexer
import java.io.Reader

/**
 * A sub class of [smaliFlexLexer] that catches errors
 * and provides methods to retrieve them.
 *
 * Doesn't start with an uppercase letter to conform with [smaliFlexLexer]'s name.
 */
class smaliCatchErrFlexLexer(
    reader: Reader,
    apiLevel: Int
) : smaliFlexLexer(reader, apiLevel) {

    private val errors = StringBuilder()
    private val syntaxErrors = mutableListOf<SyntaxError>()

    /**
     * This is kind of a hack, since [getErrorHeader] only ever gets called
     * from [nextToken] (and we can't override [nextToken] since it accesses
     * private members), this gives us the offending InvalidToken.
     */
    override fun getErrorHeader(invalidToken: InvalidToken): String {

        errors.append("[${invalidToken.line},${invalidToken.charPositionInLine}]")
        errors.append(" Error for input '${invalidToken.text}': ${invalidToken.message}")
        errors.append("\n")

        syntaxErrors += SyntaxError(
            startLine = invalidToken.line,
            startColumn = invalidToken.charPositionInLine,
            endLine = invalidToken.line,
            endColumn = invalidToken.charPositionInLine + invalidToken.text.length,
            invalidToken.message
        )

        return super.getErrorHeader(invalidToken)
    }

    fun getErrorsString() = errors.toString()

    fun getErrors(): List<SyntaxError> = syntaxErrors

}
