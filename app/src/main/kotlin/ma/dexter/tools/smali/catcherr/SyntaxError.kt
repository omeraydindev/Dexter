package ma.dexter.tools.smali.catcherr

class SyntaxError(
    val startLine: Int,
    val startColumn: Int,
    val endLine: Int,
    val endColumn: Int,
    val message: String
)
