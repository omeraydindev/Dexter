package ma.dexter.tools.decompilers.procyon

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

    override fun decompile(
        classFile: File
    ): String {

        // WARNING: This is a hack and is likely very error-prone
        val classFileName = classFile.absolutePath
            .substring(File(App.context.cacheDir, "out/").absolutePath.length)
            .replace(".class", "")
            .substring(1) // strip off the extra '/'

        // for some reason, Procyon needs to process bytecodes of 'java.lang.Class'
        // and 'java.lang.Object' before decompilation. fortunately they take up like 3 KBs
        val rtJar = File(App.context.filesDir, "rt.jar")
        extractAsset("rt.jar", rtJar)

        val options = DecompilerSettings().apply {
            // without this option, java.util.List becomes java.util.* in imports
            forceExplicitImports = true

            RT_JAR = JarFile(rtJar)
            typeLoader = ClassFileLoader(classFile, classFileName)
        }

        val output = PlainTextOutput()
        Decompiler.decompile(classFileName, output, options)

        return getBanner() + output.toString()
    }

    override fun getBanner() = """
            /*
             * Decompiled with Procyon 0.5.36.
             */
        """.trimIndent() + "\n"

    override fun getName() = "Procyon"
}
