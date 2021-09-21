package ma.dexter.tools.decompilers.jdcore

import ma.dexter.tools.decompilers.BaseJarDecompiler
import org.jd.core.v1.ClassFileToJavaSourceDecompiler
import org.jd.core.v1.api.loader.Loader
import org.jd.core.v1.api.printer.Printer
import java.io.File
import java.lang.StringBuilder
import java.nio.file.Files

/**
 * Adapted from [https://github.com/java-decompiler/jd-core/tree/v1.1.3]
 */
class JDCoreDecompiler : BaseJarDecompiler {

    override fun decompile(classFile: File): String {

        val loader = object : Loader {
            override fun canLoad(internalName: String?): Boolean {
                return internalName == classFile.name
            }

            override fun load(internalName: String?): ByteArray {
                return Files.readAllBytes(classFile.toPath())
            }
        }

        val printer = JDCorePrinter()

        ClassFileToJavaSourceDecompiler().decompile(loader, printer, classFile.name)

        return getBanner() + printer.toString()
    }

    private class JDCorePrinter : Printer {
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

    override fun getBanner() = """
            /*
             * Decompiled with JD-Core v1.1.3.
             */
        """.trimIndent() + "\n"

    override fun getName() = "JD-Core"
}
