package ma.dexter.tools.decompilers.cfr

import org.benf.cfr.reader.api.ClassFileSource
import org.benf.cfr.reader.bytecode.analysis.parse.utils.Pair
import java.util.jar.JarFile

class CFRJarSource(
    private val jarFile: JarFile
) : ClassFileSource {

    // path -> somePackage/SomeClass.class
    override fun getClassFileContent(path: String?): Pair<ByteArray, String> {
        val entry = jarFile.getJarEntry(path)

        jarFile.getInputStream(entry).use {
            return Pair(it.readBytes(), path)
        }
    }

    override fun addJar(jarPath: String?) = mutableListOf<String>()

    override fun informAnalysisRelativePathDetail(usePath: String?, classFilePath: String?) {}

    override fun getPossiblyRenamedPath(path: String?) = path
}
