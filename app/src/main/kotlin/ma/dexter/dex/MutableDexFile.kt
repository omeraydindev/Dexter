package ma.dexter.dex

import org.jf.dexlib2.Opcodes
import org.jf.dexlib2.dexbacked.DexBackedDexFile
import org.jf.dexlib2.iface.ClassDef
import org.jf.dexlib2.util.DexUtil
import org.jf.dexlib2.writer.io.FileDataStore
import org.jf.dexlib2.writer.pool.DexPool
import java.io.File

class MutableDexFile(
    val dexFile: File?,
    byteArray: ByteArray
) {
    private val dexVersion = DexUtil.verifyDexHeader(byteArray, 0)
    private val opcodes = Opcodes.forDexVersion(dexVersion)
    private val dexBackedDexFile = DexBackedDexFile(opcodes, byteArray) // to simplify reading stuff

    private val _classes = dexBackedDexFile.classes.map {
        MutableClassDef(this, it)
    }.toMutableList()

    val classes = _classes as List<MutableClassDef>

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
