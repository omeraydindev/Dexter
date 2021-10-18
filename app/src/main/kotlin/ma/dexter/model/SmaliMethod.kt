package ma.dexter.model

class SmaliMethod(
    descriptor: String,
    line: Int,
    var methodBody: String = ""
): SmaliMember(descriptor, line)
