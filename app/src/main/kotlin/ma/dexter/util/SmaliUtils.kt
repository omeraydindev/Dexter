package ma.dexter.util

import org.antlr.runtime.Token
import org.jf.smali.smaliFlexLexer
import org.jf.smali.smaliParser
import java.io.StringReader

/**
 * group 1: full method descriptor
 * group 2: method name
 * group 3: method params + return type
 */
val METHOD_DIRECTIVE_REGEX = Regex(
    """^\.method (?:(?:[a-z\-]*) )*((.*?)\((.*))${'$'}"""
)

/**
 * group 1: full field descriptor
 * group 2: field name
 * group 3: field return type
 */
val FIELD_DIRECTIVE_REGEX = Regex(
    """^\.field (?:(?:[a-z\-]*) )*((.*?):(.*))${'$'}"""
)

val FIELD_METHOD_CALL_REGEX = Regex(
    """^.*?((L.*?;)\s*->\s*(.*))${'$'}""", RegexOption.DOT_MATCHES_ALL
)

const val END_METHOD_DIRECTIVE = ".end method"

/**
 * Ltest/aaa; -> test/aaa
 */
fun normalizeSmaliPath(classDefType: String) =
    if (classDefType.startsWith("L") && classDefType.endsWith(";")) {
        classDefType.substring(1, classDefType.length - 1)
    } else {
        classDefType
    }

/**
 * Ltest/aaa; -> aaa
 */
fun getClassNameFromSmaliPath(classDefType: String) =
    normalizeSmaliPath(classDefType).substringAfterLast("/")

/**
 * Utility method to tokenize smali.
 */
inline fun tokenizeSmali(
    smaliCode: String,
    callback: (token: Token, line: Int, column: Int) -> Unit
) {
    val lexer = smaliFlexLexer(StringReader(smaliCode), 31)
    lexer.setSuppressErrors(true)

    var token: Token

    while (true) {
        token = lexer.nextToken()

        if (token == null || token.type == smaliParser.EOF) break
        if (token.type == smaliParser.WHITE_SPACE) continue

        val line = token.line - 1
        val column = token.charPositionInLine

        callback(token, line, column)
    }
}
