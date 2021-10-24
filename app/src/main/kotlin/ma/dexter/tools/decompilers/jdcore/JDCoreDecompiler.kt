package ma.dexter.tools.decompilers.jdcore

import ma.dexter.tools.decompilers.BaseJarDecompiler
import org.jd.core.v1.ClassFileToJavaSourceDecompiler
import java.io.File
import java.util.jar.JarFile

/*
 * Adapted from https://github.com/java-decompiler/jd-core/tree/v1.1.3
 */
class JDCoreDecompiler : BaseJarDecompiler {

    override fun decompileJar(
        className: String,
        jarFile: File
    ): String {

        val loader = JDLoader(JarFile(jarFile))
        val printer = JDPrinter()

        ClassFileToJavaSourceDecompiler()
            .decompile(loader, printer, className)

        return getBanner() + printer.toString()
    }

    override fun getBanner() = """
            /*
             * Decompiled with JD-Core v1.1.3.
             */
        """.trimIndent() + "\n"

    override fun getName() = "JD-Core"
}
