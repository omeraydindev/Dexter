package ma.dexter.tools.decompilers

import java.io.File

interface BaseJarDecompiler: BaseDecompiler {

    fun decompile(classFile: File): String

}
