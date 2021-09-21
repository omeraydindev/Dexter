package ma.dexter.tools.smali.catcherr

import org.antlr.runtime.tree.CommonTreeNodeStream
import org.jf.smali.smaliTreeWalker
import java.lang.StringBuilder

/**
 * A sub class of [smaliTreeWalker] that catches exceptions
 * and provides a method ([getErrors]) to retrieve them.
 *
 * Doesn't start with an uppercase letter to conform with [smaliTreeWalker]'s name.
 */
class smaliCatchErrTreeWalker(
    treeStream: CommonTreeNodeStream
): smaliTreeWalker(treeStream) {

    private val errors = StringBuilder()

    override fun emitErrorMessage(msg: String?) {
        errors.append(msg)
        errors.append("\n")
    }

    fun getErrors() = errors.toString()

}
