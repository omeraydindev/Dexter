package ma.dexter.parsers.smali

class SmaliFile(
    methods: List<SmaliMethod>,
    fields: List<SmaliField>
) {
    val members = methods + fields
}

sealed class SmaliMember(
    val descriptor: String,
    val name: String,
    val line: Int
)

class SmaliMethod(
    descriptor: String,
    name: String,
    line: Int,
    var methodBody: String = ""
): SmaliMember(descriptor, name, line)

class SmaliField(
    descriptor: String,
    name: String,
    line: Int
): SmaliMember(descriptor, name, line)
