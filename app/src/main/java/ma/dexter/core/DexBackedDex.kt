package ma.dexter.core

import com.google.common.io.ByteStreams
import org.jf.dexlib2.Opcodes
import org.jf.dexlib2.dexbacked.DexBackedClassDef
import org.jf.dexlib2.dexbacked.DexBackedDexFile
import org.jf.dexlib2.dexbacked.raw.ItemType
import org.jf.dexlib2.util.DexUtil
import java.io.*

/**
 * Wrapper class around [DexBackedDexFile] for easier access to dexVersion, methodIdCount etc.
 */
class DexBackedDex(
    opcodes: Opcodes?,
    buf: ByteArray,
    offset: Int,
    verifyMagic: Boolean
): DexBackedDexFile(opcodes, buf, offset, verifyMagic) {

    var dexVersion = 0

    /*
     * Bit of a hack to retrieve the dex version without re-reading the dex file.
     * (This method only ever gets called from the constructor to get the dex version.)
     */
    override fun getVersion(buf: ByteArray?, offset: Int, verifyMagic: Boolean): Int {
        dexVersion = super.getVersion(buf, offset, verifyMagic)

        return dexVersion
    }

    val methodIdCount: Int?
        get() = getMapItemForSection(ItemType.METHOD_ID_ITEM)?.itemCount

    val fieldIdCount: Int?
        get() = getMapItemForSection(ItemType.FIELD_ID_ITEM)?.itemCount

    val protoIdCount: Int?
        get() = getMapItemForSection(ItemType.PROTO_ID_ITEM)?.itemCount

    val typeIdCount: Int?
        get() = getMapItemForSection(ItemType.TYPE_ID_ITEM)?.itemCount

    /**
     * Finds [DexBackedClassDef] by its String descriptor.
     *
     * Returns null if no match is found.
     */
    fun findClassDefByDescriptor(dexClassDef: String): DexBackedClassDef? {
        classSection.forEach {
            if (it.type == dexClassDef) {
                return it
            }
        }

        return null
    }

    companion object {

        fun fromFile(
            file: File,
            opcodes: Opcodes? = null
        ): DexBackedDex {
            return fromInputStream(BufferedInputStream(FileInputStream(file)), opcodes)
        }

        fun fromInputStream(
            inputStream: InputStream,
            opcodes: Opcodes? = null
        ): DexBackedDex {

            DexUtil.verifyDexHeader(inputStream)
            val buf = ByteStreams.toByteArray(inputStream)

            return DexBackedDex(opcodes, buf, 0, false)
        }

    }

}
