package ma.dexter.parsers.smali

import ma.dexter.util.END_METHOD_DIRECTIVE
import ma.dexter.util.FIELD_DIRECTIVE_REGEX
import ma.dexter.util.METHOD_DIRECTIVE_REGEX

fun parseSmali(
    smaliCode: String
): SmaliFile {
    val smaliMethods = mutableListOf<SmaliMethod>()
    val smaliFields = mutableListOf<SmaliField>()

    val lines = smaliCode.lines()
    lines.forEachIndexed { lineNumber, _line ->
        val line = _line.trimStart()

        // .method
        METHOD_DIRECTIVE_REGEX.matchEntire(line)?.let { result ->
            val descriptor = result.groups[1]!!.value
            val name = result.groups[2]!!.value
            val nameIndex = result.groups[2]!!.range.first

            smaliMethods += SmaliMethod(descriptor, name, nameIndex, lineNumber)
        }

        // .end method
        if (line.startsWith(END_METHOD_DIRECTIVE)) {
            smaliMethods.lastOrNull()?.let { last ->
                last.methodBody = lines
                    .slice(last.line..lineNumber)
                    .joinToString("\n")
            }
        }

        // .field
        FIELD_DIRECTIVE_REGEX.matchEntire(line)?.let { result ->
            val descriptor = result.groups[1]!!.value
            val name = result.groups[2]!!.value
            val nameIndex = result.groups[2]!!.range.first

            smaliFields += SmaliField(descriptor, name, nameIndex, lineNumber)
        }
    }

    return SmaliFile(smaliMethods, smaliFields)
}
