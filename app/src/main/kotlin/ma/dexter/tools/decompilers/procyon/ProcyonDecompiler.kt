package ma.dexter.tools.decompilers.procyon

import com.strobel.assembler.metadata.JarTypeLoader
import com.strobel.decompiler.Decompiler
import com.strobel.decompiler.DecompilerSettings
import com.strobel.decompiler.DecompilerSettings.RT_JAR
import com.strobel.decompiler.PlainTextOutput
import ma.dexter.App
import ma.dexter.tools.decompilers.BaseJarDecompiler
import ma.dexter.util.extractAsset
import java.io.File
import java.util.jar.JarFile

class ProcyonDecompiler: BaseJarDecompiler {

    override fun decompileJar(
        className: String,
        jarFile: File
    ): String {

        /**
         * For some reason, Procyon needs to process bytecodes of [java.lang.Class]
         * and [java.lang.Object] before decompilation. Fortunately they take up like 3 KBs
         */
        val rtJar = File(App.context.filesDir, "rt.jar")
        extractAsset("rt.jar", rtJar)

        val options = DecompilerSettings().apply {
            forceExplicitImports = true
            showSyntheticMembers = false

            RT_JAR = JarFile(rtJar)
            typeLoader = JarTypeLoader(JarFile(jarFile))
        }

        val output = PlainTextOutput()
        Decompiler.decompile(className, output, options)

        return getBanner() + output.toString()
    }

    override fun getBanner() = """
            /*
             * Decompiled with Procyon 0.5.36.
             */
        """.trimIndent() + "\n"

    override fun getName() = "Procyon"
}
