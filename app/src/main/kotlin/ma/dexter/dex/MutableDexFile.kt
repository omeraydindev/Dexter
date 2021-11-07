package ma.dexter.dex

import ma.dexter.util.DEFAULT_DEX_VERSION
import org.jf.dexlib2.Opcodes
import org.jf.dexlib2.dexbacked.DexBackedDexFile
import org.jf.dexlib2.iface.ClassDef
import org.jf.dexlib2.util.DexUtil
import org.jf.dexlib2.writer.io.FileDataStore
import org.jf.dexlib2.writer.pool.DexPool
import java.io.File

class MutableDexFile {
    private val dexVersion: Int
    private val _classes: MutableList<MutableClassDef>

    val dexFile: File? // todo
    val opcodes: Opcodes
    val classes: List<MutableClassDef>
        get() = _classes

    constructor(
        dexFile: File?,
        byteArray: ByteArray
    ) {
        this.dexFile = dexFile
        this.dexVersion = DexUtil.verifyDexHeader(byteArray, 0)
        this.opcodes = Opcodes.forDexVersion(dexVersion)

        val dexBacked = DexBackedDexFile(opcodes, byteArray)
        this._classes = dexBacked.classes.map {
            MutableClassDef(this, it)
        }.toMutableList()
    }

    constructor(
        classDefs: List<MutableClassDef> = listOf(),
        dexVersion: Int? = null
    ) {
        this.dexFile = null
        this.dexVersion =
            dexVersion ?: classDefs.maxOfOrNull { it.parentDex.dexVersion } ?: DEFAULT_DEX_VERSION
        this.opcodes = Opcodes.forDexVersion(this.dexVersion)

        this._classes = classDefs.toMutableList()
    }


    /**
     * Adds a [ClassDef] if it doesn't exist.
     */
    fun addClassDef(classDef: ClassDef) {
        val def = MutableClassDef(this, classDef)

        if (def !in _classes) {
            _classes.add(def)
        }
    }

    /**
     * Replaces a [ClassDef] if it exists.
     */
    fun replaceClassDef(classDef: ClassDef) {
        val def = MutableClassDef(this, classDef)
        val index = _classes.indexOf(def)

        if (index != -1) {
            _classes[index] = def
        }
    }

    /**
     * Finds [MutableClassDef] from the classDescriptor specified,
     * returns null if no match is found.
     */
    fun findClassDef(classDescriptor: String?) =
        _classes.firstOrNull { it.type == classDescriptor }

    /**
     * Deletes a [ClassDef] if it exists.
     */
    fun deleteClassDef(classDescriptor: String): Boolean {
        val element = _classes.find { it.type == classDescriptor }
        if (element != null) {
            return deleteClassDef(element)
        }
        return false
    }

    /**
     * Deletes a [ClassDef] if it exists.
     */
    fun deleteClassDef(classDef: MutableClassDef): Boolean {
        return _classes.remove(classDef)
    }

    /**
     * Deletes [MutableClassDef]s in the given package.
     *
     * [packagePath] being in somePackage/someClass format.
     */
    fun deletePackage(packagePath: String) {
        _classes.removeAll { it.type.startsWith("L$packagePath/") }
    }

    /**
     * Writes to the [file] specified.
     */
    fun writeToFile(
        file: File
    ) {
        val dexPool = DexPool(opcodes)
        _classes.forEach {
            dexPool.internClass(it.classDef)
        }
        dexPool.writeTo(FileDataStore(file))
    }

}
