package ma.dexter.tools.smali.catcherr

import org.antlr.runtime.CommonTokenStream
import org.jf.smali.smaliParser
import java.lang.StringBuilder

/**
 * A sub class of [smaliParser] that catches exceptions
 * and provides a method ([getErrors]) to retrieve them.
 *
 * Doesn't start with an uppercase letter to conform with [smaliParser]'s name.
 */
class smaliCatchErrParser(
    tokens: CommonTokenStream
): smaliParser(tokens) {

    private val errors = StringBuilder()

    override fun emitErrorMessage(msg: String?) {
        errors.append(msg)
        errors.append("\n")
    }

    fun getErrors() = errors.toString()

}
