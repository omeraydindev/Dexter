package ma.dexter.tools.decompilers.procyon

import com.strobel.assembler.metadata.Buffer
import com.strobel.assembler.metadata.ITypeLoader
import com.strobel.assembler.metadata.JarTypeLoader
import java.io.File
import java.io.FileInputStream

/**
 * This is temporary, will be migrating to TODO: [JarTypeLoader].
 */
class ClassFileLoader(
    private val classFile: File,
    private val classFileInternalName: String
): ITypeLoader {

    override fun tryLoadType(internalName: String, buffer: Buffer): Boolean {

        if (classFileInternalName != internalName) {
            return false
        }

        val inputStream = FileInputStream(classFile)
        var remainingBytes = inputStream.available()

        buffer.reset(remainingBytes)

        while (remainingBytes > 0) {
            val bytesRead = inputStream.read(buffer.array(), buffer.position(), remainingBytes)
            if (bytesRead < 0) {
                break
            }
            buffer.position(buffer.position() + bytesRead)
            remainingBytes -= bytesRead
        }

        buffer.position(0)

        return true
    }

}
