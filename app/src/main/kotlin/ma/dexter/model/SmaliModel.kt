package ma.dexter.model

import ma.dexter.util.END_METHOD_DIRECTIVE
import ma.dexter.util.FIELD_DIRECTIVE_REGEX
import ma.dexter.util.METHOD_DIRECTIVE_REGEX

class SmaliModel private constructor(
    val smaliCode: String,
    val smaliMethods: List<SmaliMethod>,
    val smaliFields: List<SmaliField>,
) {
    val smaliMembers
        get() = smaliFields + smaliMethods

    companion object {

        fun create(smaliCode: String) = parse(smaliCode)

        // Obviously using a Parser is better, but this is good enough for now.
        private fun parse(smaliCode: String): SmaliModel {
            val smaliMethods = mutableListOf<SmaliMethod>()
            val smaliFields = mutableListOf<SmaliField>()

            val lines = smaliCode.lines()
            lines.forEachIndexed { lineNumber, _line ->
                val line = _line.trimStart()

                // .method
                METHOD_DIRECTIVE_REGEX.matchEntire(line)?.let { result ->
                    result.groups[1]?.let { match ->
                        smaliMethods += SmaliMethod(match.value, lineNumber)
                    }
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
                    result.groups[1]?.let { match ->
                        smaliFields += SmaliField(match.value, lineNumber)
                    }
                }

            }

            return SmaliModel(
                smaliCode, smaliMethods, smaliFields
            )
        }

    }
}
