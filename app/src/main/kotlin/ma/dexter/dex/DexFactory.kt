package ma.dexter.dex

import com.google.common.io.ByteStreams
import org.jf.dexlib2.Opcodes
import org.jf.dexlib2.dexbacked.DexBackedDexFile
import org.jf.dexlib2.util.DexUtil
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

object DexFactory {

    fun fromFile(
        file: File,
        opcodes: Opcodes? = null
    ): MutableDex {
        BufferedInputStream(FileInputStream(file)).use {
            return fromInputStream(it, opcodes)
        }
    }

    /**
     * Does NOT close the [inputStream] passed.
     */
    private fun fromInputStream(
        inputStream: InputStream,
        opcodes: Opcodes? = null
    ): MutableDex {
        return fromByteArray(ByteStreams.toByteArray(inputStream), opcodes)
    }

    fun fromByteArray(
        byteArray: ByteArray,
        opcodes: Opcodes? = null
    ): MutableDex {
        DexUtil.verifyDexHeader(byteArray, 0)

        return MutableDex(DexBackedDexFile(opcodes, byteArray))
    }

}
