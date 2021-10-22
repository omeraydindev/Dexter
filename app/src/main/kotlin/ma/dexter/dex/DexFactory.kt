package ma.dexter.dex

import org.jf.dexlib2.util.DexUtil
import java.io.File

object DexFactory {

    fun fromFile(
        file: File
    ): MutableDexFile {
        return fromByteArray(file.readBytes(), file)
    }

    fun fromByteArray(
        byteArray: ByteArray,
        file: File? = null
    ): MutableDexFile {
        DexUtil.verifyDexHeader(byteArray, 0)

        return MutableDexFile(file, byteArray)
    }

}
