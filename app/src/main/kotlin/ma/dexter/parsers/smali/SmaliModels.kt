package ma.dexter.parsers.smali

class SmaliFile(
    methods: List<SmaliMethod>,
    fields: List<SmaliField>
) {
    val members = fields + methods
}

sealed class SmaliMember(
    val descriptor: String,
    val name: String,
    val nameIndex: Int,
    val line: Int
)

class SmaliMethod(
    descriptor: String,
    name: String,
    nameIndex: Int,
    line: Int,
    var methodBody: String = ""
): SmaliMember(descriptor, name, nameIndex, line)

class SmaliField(
    descriptor: String,
    name: String,
    nameIndex: Int,
    line: Int
): SmaliMember(descriptor, name, nameIndex, line)
