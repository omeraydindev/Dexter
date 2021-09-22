package ma.dexter.core.model

class SmaliMethod(
    descriptor: String,
    line: Int,
    var methodBody: String = ""
): SmaliMember(descriptor, line)
