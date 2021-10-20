package ma.dexter.tools.jar

import ma.dexter.BuildConfig
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.jar.Attributes
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import java.util.jar.Manifest

class JarTool(
    private val classesDir: File,
    private val jarFile: File,
    private val attributes: Attributes = getDefAttrs()
) {

    fun create(): File {
        val manifest = buildManifest(attributes)

        FileOutputStream(jarFile).use { stream ->
            JarOutputStream(stream, manifest).use { out ->
                val files = classesDir.listFiles()

                files?.forEach {
                    add(classesDir.path, it, out)
                }
            }
        }

        return jarFile
    }

    // TODO: clean this mess up
    private fun add(
        parentPath: String,
        source: File,
        target: JarOutputStream
    ) {
        var name = source.path.substring(parentPath.length + 1)

        if (source.isDirectory) {
            if (name.isNotEmpty()) {
                if (!name.endsWith("/")) name += "/"

                JarEntry(name).run {
                    time = source.lastModified()
                    target.putNextEntry(this)
                    target.closeEntry()
                }
            }

            source.listFiles()!!.forEach { nestedFile ->
                add(parentPath, nestedFile, target)
            }

            return
        }

        JarEntry(name).run {
            time = source.lastModified()
            target.putNextEntry(this)
        }

        BufferedInputStream(FileInputStream(source)).use {
            val buffer = ByteArray(1024)
            while (true) {
                val count = it.read(buffer)
                if (count == -1) break
                target.write(buffer, 0, count)
            }

            target.closeEntry()
        }
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
