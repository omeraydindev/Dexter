package ma.dexter.tools.decompilers.fernflower

import ma.dexter.tools.decompilers.BaseJarDecompiler
import org.jetbrains.java.decompiler.main.decompiler.BaseDecompiler
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences
import java.io.File

/*
 * Adapted from [https://github.com/JetBrains/intellij-community/blob/master/plugins/java-decompiler/plugin/src/org/jetbrains/java/decompiler/IdeaDecompiler.kt]
 */
class FernflowerDecompiler : BaseJarDecompiler {
    private val options = defaultOptions()

    /**
     * Decompiles given [jarFile] to Java using Fernflower.
     *
     * @return Decompiled Java code
     */
    override fun decompileJar(
        className: String,
        jarFile: File
    ): String {
        val bytecodeProvider = FFBytecodeProvider()
        val resultSaver = FFResultSaver(className)

        val logger = object : IFernflowerLogger() {
            override fun writeMessage(p0: String?, p1: Severity?) {}

            override fun writeMessage(p0: String?, p1: Severity?, p2: Throwable?) {}
        }

        val decompiler = BaseDecompiler(bytecodeProvider, resultSaver, options, logger)
        decompiler.addSource(jarFile)
        decompiler.decompileContext()

        return resultSaver.result.ifEmpty {
            "// Error: Fernflower couldn't decompile $className"
        }
    }

    override fun getBanner() = """
            /*
             * Decompiled with Fernflower [e7fa2769f5].
             */
        """.trimIndent() + "\n"

    override fun getName() = "Fernflower"

    private fun defaultOptions() = mapOf(
        IFernflowerPreferences.HIDE_DEFAULT_CONSTRUCTOR to "0",
        IFernflowerPreferences.DECOMPILE_GENERIC_SIGNATURES to "1",
        IFernflowerPreferences.REMOVE_SYNTHETIC to "1",
        IFernflowerPreferences.REMOVE_BRIDGE to "1",
        IFernflowerPreferences.LITERALS_AS_IS to "1",
        IFernflowerPreferences.NEW_LINE_SEPARATOR to "1",
        IFernflowerPreferences.BANNER to getBanner(),
        IFernflowerPreferences.MAX_PROCESSING_METHOD to 60,
        IFernflowerPreferences.IGNORE_INVALID_BYTECODE to "1",
        IFernflowerPreferences.VERIFY_ANONYMOUS_CLASSES to "1"
    )
}
