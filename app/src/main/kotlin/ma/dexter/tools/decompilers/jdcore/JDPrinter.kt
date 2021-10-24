package ma.dexter.tools.decompilers.jdcore

import org.jd.core.v1.api.printer.Printer

class JDPrinter : Printer {
    private var indentationCount = 0
    private var sb = StringBuilder()

    override fun toString() = sb.toString()

    override fun start(maxLineNumber: Int, majorVersion: Int, minorVersion: Int) {}

    override fun end() {}

    override fun printText(text: String?) {
        sb.append(text)
    }

    override fun printNumericConstant(constant: String?) {
        sb.append(constant)
    }

    override fun printStringConstant(constant: String?, ownerInternalName: String?) {
        sb.append(constant)
    }

    override fun printKeyword(keyword: String?) {
        sb.append(keyword)
    }

    override fun printDeclaration(
        type: Int,
        internalTypeName: String?,
        name: String?,
        descriptor: String?
    ) {
        sb.append(name)
    }

    override fun printReference(
        type: Int,
        internalTypeName: String?,
        name: String?,
        descriptor: String?,
        ownerInternalName: String?
    ) {
        sb.append(name)
    }

    override fun indent() {
        indentationCount++
    }

    override fun unindent() {
        indentationCount--
    }

    override fun startLine(lineNumber: Int) {
        for (i in 0 until indentationCount) sb.append("    ")
    }

    override fun endLine() {
        sb.append("\n")
    }

    override fun extraLine(count: Int) {
        var n = count
        while (n-- > 0) sb.append("\n")
    }

    override fun startMarker(type: Int) {}

    override fun endMarker(type: Int) {}
}
