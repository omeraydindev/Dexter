package ma.dexter.tools.d2j

import com.googlecode.d2j.converter.IR2JConverter
import com.googlecode.d2j.dex.*
import com.googlecode.d2j.node.DexFileNode
import com.googlecode.d2j.node.DexMethodNode
import com.googlecode.d2j.reader.BaseDexFileReader
import com.googlecode.d2j.reader.DexFileReader
import com.googlecode.dex2jar.ir.IrMethod
import com.googlecode.dex2jar.ir.stmt.LabelStmt
import com.googlecode.dex2jar.ir.stmt.Stmt
import com.googlecode.dex2jar.tools.Constants
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import java.io.File
import java.nio.file.Files
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class D2JFacade(
    private val reader: BaseDexFileReader,
    private val options: D2JOptions,
    private val exceptionHandler: DexExceptionHandler,
    private val progressConsumer: D2JProgressConsumer,
) {

    fun convert(
        jarFile: File,
    ) {
        Files.newOutputStream(jarFile.toPath()).use { fos ->
            ZipOutputStream(fos).use { zos ->
                convert { className, bytes ->
                    val zipEntry = ZipEntry("$className.class")
                    zos.putNextEntry(zipEntry)
                    zos.write(bytes)
                    zos.closeEntry()
                }
            }
        }
    }

    private fun convert(
        conversionConsumer: D2JConversionConsumer,
    ) {
        val currentClassCount = AtomicInteger()
        val totalClassCount = reader.classNames.size

        val fileNode = DexFileNode()
        try {
            val config = options.getReaderConfig() or DexFileReader.IGNORE_READ_EXCEPTION
            reader.accept(fileNode, config)
        } catch (ex: Exception) {
            exceptionHandler.handleFileException(ex)
        }

        val cvf = ClassVisitorFactory {
            val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)
            val rca = LambadaNameSafeClassAdapter(cw)

            return@ClassVisitorFactory object : ClassVisitor(Constants.ASM_VERSION, rca) {
                override fun visitEnd() {
                    super.visitEnd()

                    val className = rca.className
                    progressConsumer(className, currentClassCount.incrementAndGet(), totalClassCount)

                    val data: ByteArray
                    try {
                        // FIXME handle 'java.lang.RuntimeException: Method code too large!'
                        data = cw.toByteArray()
                    } catch (ex: java.lang.Exception) {
                        System.err.println("ASM failed to generate .class file: $className")
                        exceptionHandler.handleFileException(ex)
                        return
                    }

                    conversionConsumer(className, data)
                }
            }
        }

        val dex2Asm = object : ExDex2Asm(exceptionHandler) {
            override fun convertCode(methodNode: DexMethodNode, mv: MethodVisitor?) {
                if (options.getReaderConfig() and DexFileReader.SKIP_CODE != 0 && methodNode.method.name == "<clinit>") {
                    // also skip clinit
                    return
                }
                super.convertCode(methodNode, mv)
            }

            override fun optimize(irMethod: IrMethod) {
                T_CLEAN_LABEL.transform(irMethod)
                /*if (0 != (v3Config & V3.TOPOLOGICAL_SORT)) {
                    // T_topologicalSort.transform(irMethod);
                }*/
                T_DEAD_CODE.transform(irMethod)
                T_REMOVE_LOCAL.transform(irMethod)
                T_REMOVE_CONST.transform(irMethod)
                T_ZERO.transform(irMethod)
                if (T_NPE.transformReportChanged(irMethod)) {
                    T_DEAD_CODE.transform(irMethod)
                    T_REMOVE_LOCAL.transform(irMethod)
                    T_REMOVE_CONST.transform(irMethod)
                }
                T_NEW.transform(irMethod)
                T_FILL_ARRAY.transform(irMethod)
                T_AGG.transform(irMethod)
                T_MULTI_ARRAY.transform(irMethod)
                T_VOID_INVOKE.transform(irMethod)
                if (0 != options.getV3Config() and V3.PRINT_IR) {
                    var i = 0
                    for (p in irMethod.stmts) {
                        if (p.st == Stmt.ST.LABEL) {
                            val labelStmt = p as LabelStmt
                            labelStmt.displayName = "L" + i++
                        }
                    }
                    println(irMethod)
                }
                T_TYPE.transform(irMethod)
                T_UNSSA.transform(irMethod)
                T_IR_2_J_REG_ASSIGN.transform(irMethod)
                T_TRIM_EX.transform(irMethod)
            }

            override fun ir2j(irMethod: IrMethod?, mv: MethodVisitor?) {
                val iR2J = IR2JConverter(0 != V3.OPTIMIZE_SYNCHRONIZED and options.getV3Config())
                iR2J.convert(irMethod, mv)
            }
        }

        dex2Asm.convertDex(fileNode, cvf)
    }
}
