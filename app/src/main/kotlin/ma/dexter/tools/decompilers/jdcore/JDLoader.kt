package ma.dexter.tools.decompilers.jdcore

import org.jd.core.v1.api.loader.Loader
import java.util.jar.JarFile

class JDLoader(
    private val jarFile: JarFile
) : Loader {

    override fun canLoad(internalName: String?): Boolean {
        return jarFile.getJarEntry("$internalName.class") != null
    }

    override fun load(internalName: String?): ByteArray? {
        val entry = jarFile.getJarEntry("$internalName.class") ?: return null

        jarFile.getInputStream(entry).use {
            return it.readBytes()
        }
    }

}
