package ma.dexter.dex

import org.jf.dexlib2.Opcodes
import org.jf.dexlib2.dexbacked.DexBackedDexFile
import org.jf.dexlib2.iface.ClassDef
import org.jf.dexlib2.util.DexUtil
import org.jf.dexlib2.writer.io.FileDataStore
import org.jf.dexlib2.writer.pool.DexPool
import java.io.File

class MutableDexFile {
    private val dexVersion: Int
    private val opcodes: Opcodes
    private val _classes: MutableList<MutableClassDef>

    val dexFile: File?
    val classes: List<MutableClassDef>

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
        this.classes = _classes
    }

    constructor(
        classDefs: List<MutableClassDef>,
        opcodes: Opcodes = Opcodes.getDefault()
    ) {
        this.dexFile = null
        this.dexVersion = 0
        this.opcodes = opcodes

        this._classes = classDefs.toMutableList()
        this.classes = _classes
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
    fun deleteClassDef(classDef: MutableClassDef) {
        _classes.remove(classDef)
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
