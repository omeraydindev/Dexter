package ma.dexter.parsers.java

class JavaFile(
    val members: List<JavaMember>
)

sealed class JavaMember(
    val name: String,
    val line: Int
)

class JavaField(
    name: String,
    line: Int
) : JavaMember(name, line)

class JavaMethod(
    name: String,
    line: Int,
) : JavaMember(name, line)

class JavaInnerClass(
    name: String,
    line: Int
) : JavaMember(name, line)
