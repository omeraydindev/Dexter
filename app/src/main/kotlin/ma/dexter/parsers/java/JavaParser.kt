package ma.dexter.parsers.java

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.CallableDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.nodeTypes.NodeWithImplements

/**
 * Parses given [javaCode] to fields/methods/inner-classes
 * on the first level of the first top level class found.
 */
fun parseJava(
    javaCode: String
): JavaFile {
    val members = mutableListOf<JavaMember>()
    val javaFile = JavaFile(members)

    val compilationUnit = JavaParser()
        .parse(javaCode)
        .result.get()

    // NodeWithImplements -> ClassOrInterfaceDeclaration, EnumDeclaration, RecordDeclaration
    val topClass = compilationUnit.types
        .firstOrNull { it is NodeWithImplements<*> }
        ?: return javaFile

    topClass.childNodes.filter(Node::hasRange).forEach { node ->

        if (node is FieldDeclaration) {
            members += JavaField(
                name = node.getVariable(0).nameAsString,
                line = node.getLine()
            )
        }

        // CallableDeclaration -> ConstructorDeclaration, MethodDeclaration
        if (node is CallableDeclaration<*>) {
            members += JavaMethod(
                name = node.nameAsString,
                line = node.getLine()
            )
        }

        if (node is ClassOrInterfaceDeclaration) {
            members += JavaInnerClass(
                name = node.nameAsString,
                line = node.getLine()
            )
        }

    }

    return javaFile
}

fun Node.getLine() =
    range.get().begin.line - 1
