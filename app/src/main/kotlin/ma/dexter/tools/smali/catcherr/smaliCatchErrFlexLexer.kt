package ma.dexter.tools.smali.catcherr

import org.jf.smali.InvalidToken
import org.jf.smali.smaliFlexLexer
import java.io.Reader
import java.lang.StringBuilder

/**
 * A sub class of [smaliFlexLexer] that catches exceptions
 * and provides a method ([getErrors]) to retrieve them.
 *
 * Doesn't start with an uppercase letter to conform with [smaliFlexLexer]'s name.
 */
class smaliCatchErrFlexLexer(
    reader: Reader,
    apiLevel: Int
): smaliFlexLexer(reader, apiLevel) {

    private val errors = StringBuilder()

    /**
     * This is kind of a hack, since [getErrorHeader] only ever gets called
     * from [nextToken] (and we can't override [nextToken] since it accesses
     * private members), this gives us the offending InvalidToken.
     */
    override fun getErrorHeader(invalidToken: InvalidToken): String {

        errors.append("[${invalidToken.line},${invalidToken.charPositionInLine}]")
        errors.append(" Error for input '${invalidToken.text}': ${invalidToken.message}")
        errors.append("\n")

        return super.getErrorHeader(invalidToken)
    }

    fun getErrors() = errors.toString()
}
