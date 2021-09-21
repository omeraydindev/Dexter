package ma.dexter.tools.jar

import ma.dexter.BuildConfig
import java.io.File
import java.io.FileOutputStream
import java.util.jar.Attributes
import java.util.jar.JarOutputStream
import java.util.jar.Manifest

class JarCreator(
    private val classesDir: File,
    private val jarFile: File,
    private val attributes: Attributes = getDefAttrs()
) {

    fun create(): File {
        val manifest = buildManifest(attributes)

        FileOutputStream(jarFile).use { stream ->
            JarOutputStream(stream, manifest).use { out ->
                val files = classesDir.listFiles()

                if (files != null) {
                    for (clazz in files) {
                        JarPackager.add(classesDir.path, clazz, out)
                    }
                }
            }
        }

        return jarFile
    }

    private fun add(
        parentPath: String,
        source: File,
        target: JarOutputStream
    ) {
        var name = source.path.substring(parentPath.length + 1)

        // todo
    }

    companion object {

        private fun getDefAttrs(): Attributes {
            return Attributes().also {
                it[Attributes.Name("Created-By")] = BuildConfig.APPLICATION_ID
            }
        }

        private fun buildManifest(options: Attributes): Manifest {
            return Manifest().also {
                it.mainAttributes[Attributes.Name.MANIFEST_VERSION] = "1.0"
                it.mainAttributes.putAll(options)
            }
        }
    }

}
