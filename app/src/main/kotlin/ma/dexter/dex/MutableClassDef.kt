package ma.dexter.dex

import org.jf.dexlib2.iface.ClassDef
import org.jf.dexlib2.iface.Method
import org.jf.dexlib2.iface.reference.TypeReference

class MutableClassDef(
    val parentDex: MutableDexFile,
    val classDef: ClassDef
): Comparable<String> {

    val accessFlags: Int
        get() = classDef.accessFlags

    val type: String // class descriptor
        get() = classDef.type

    val methods: Iterable<Method>
        get() = classDef.methods

    // add more fields when necessary

    /**
     * See [TypeReference.equals]
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other !is MutableClassDef) return false

        return classDef.toString() == other.toString()
    }

    /**
     * See [TypeReference.hashCode]
     */
    override fun hashCode(): Int {
        return classDef.hashCode()
    }

    /**
     * See [TypeReference.compareTo]
     */
    override fun compareTo(other: String): Int {
        return classDef.compareTo(other)
    }

    /**
     * See [TypeReference.toString]
     */
    override fun toString(): String {
        return classDef.toString()
    }

}
