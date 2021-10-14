package ma.dexter.util.compile

import com.android.tools.r8.CompilationMode
import com.android.tools.r8.D8
import com.android.tools.r8.D8Command
import com.android.tools.r8.OutputMode
import org.eclipse.jdt.core.compiler.batch.BatchCompiler
import java.io.File
import java.io.PrintWriter

/**
 * Utility class to compile Java source to DEX easily.
 */
object Java2Dex {

    /**
     * Compiles given [javaCode] and returns the generated dex file.
     *
     * Java -`ECJ`> Java bytecode -`D8`> Dalvik bytecode
     */
    fun compile(
        fileName: String,
        javaCode: String
    ): File {
        val testFolder = File(javaClass.getResource("/Test")!!.file)

        // Init files
        val rtJar    = File(testFolder, "rt.jar")
        val javaFile = File(testFolder, fileName).apply { delete() }
        val classDir = File(testFolder, "classes/").apply { deleteRecursively(); mkdirs() }
        val dexFile  = File(testFolder, "classes.dex").apply { delete() }

        // Write the Java code to file
        javaFile.writeText(javaCode)

        // Java -> Java bytecode
        runECJ(javaFile, classDir, rtJar)

        // Java bytecode -> Dalvik bytecode
        runD8(dexFile, classDir, testFolder, rtJar)

        return dexFile
    }

    private fun runECJ(
        javaFile: File,
        classDir: File,
        rtJar: File
    ) {
        // -g         -> keep debug info
        // -nowarn    -> disable warnings
        // -proc:none -> disable annotation processors

        val ecjResult = BatchCompiler.compile(
            "-1.8 -g -nowarn -proc:none ${javaFile.absolutePath} -d ${classDir.absolutePath} -cp ${rtJar.absolutePath}",
            PrintWriter(System.out), PrintWriter(System.err), null
        )

        if (!ecjResult) throw Java2DexException("ECJ failed to compile Java")
    }

    private fun runD8(
        dexFile: File,
        classDir: File,
        outputDir: File,
        rtJar: File
    ) {
        val d8Command = D8Command.builder()
            .addClasspathFiles(rtJar.toPath())
            .addProgramFiles(classDir
                .walk()
                .filter { it.name.endsWith(".class") }
                .map(File::toPath)
                .toList()
            )
            .setMinApiLevel(21)
            .setMode(CompilationMode.DEBUG)
            .setOutput(outputDir.toPath(), OutputMode.DexIndexed)
            .build()

        D8.run(d8Command)

        if (!dexFile.exists()) throw Java2DexException("D8 failed to convert JAR to DEX")
    }

    class Java2DexException(name: String): Exception(name)
}
