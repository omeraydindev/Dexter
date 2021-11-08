package ma.dexter.tools.d2j

import com.googlecode.d2j.Method
import com.googlecode.d2j.dex.DexExceptionHandler
import com.googlecode.d2j.node.DexMethodNode
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.io.PrintWriter
import java.io.StringWriter

/**
 * See [com.googlecode.dex2jar.tools.BaksmaliBaseDexExceptionHandler]
 */
class D2JExceptionHandler : DexExceptionHandler {

    private val exceptionMap = mutableMapOf<DexMethodNode, Exception>()
    private var fileExceptions = mutableListOf<Exception>()

    fun hasException(): Boolean {
        return exceptionMap.isNotEmpty() || fileExceptions.isNotEmpty()
    }

    fun getExceptions(): String {
        if (!hasException()) return ""

        return buildString {
            append("File exceptions:\n")
            fileExceptions.forEach {
                append(it.stackTraceToString())
                append("\n")
            }

            append("\n")

            append("Method exceptions:\n")
            exceptionMap.forEach {
                append(it.value.stackTraceToString())
                append("\n")
            }
        }
    }

    override fun handleFileException(e: Exception) {
        fileExceptions += e
    }

    /**
     * Adopted from [com.googlecode.d2j.dex.BaseDexExceptionHandler.handleMethodTranslateException]
     */
    override fun handleMethodTranslateException(
        method: Method,
        methodNode: DexMethodNode,
        mv: MethodVisitor,
        e: Exception
    ) {
        exceptionMap[methodNode] = e

        // replace the generated code with
        // 'return new RuntimeException("d2j failed to translate: exception");'
        val s = StringWriter()
        s.append("d2j failed to translate: ")
        e.printStackTrace(PrintWriter(s))

        mv.visitTypeInsn(Opcodes.NEW, "java/lang/RuntimeException")
        mv.visitInsn(Opcodes.DUP)
        mv.visitLdcInsn(s.toString())
        mv.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            "java/lang/RuntimeException",
            "<init>",
            "(Ljava/lang/String;)V",
            false
        )
        mv.visitInsn(Opcodes.ATHROW)
    }
}
