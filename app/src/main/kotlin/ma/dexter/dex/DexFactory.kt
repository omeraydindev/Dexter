package ma.dexter.dex

import org.jf.dexlib2.util.DexUtil
import java.io.File

object DexFactory {

    fun fromFile(
        file: File
    ): MutableDexFile {
        return fromByteArray(file.readBytes(), file)
    }

    private fun fromByteArray(
        byteArray: ByteArray,
        file: File
    ): MutableDexFile {
        DexUtil.verifyDexHeader(byteArray, 0)

        return MutableDexFile(file, byteArray)
    }

}
