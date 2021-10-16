package ma.dexter.dex

import org.jf.dexlib2.Opcodes
import org.jf.dexlib2.iface.ClassDef
import org.jf.dexlib2.iface.DexFile

class MutableDex(
    dexFile: DexFile
): DexFile {
    private val _classes = dexFile.classes.toMutableList()
    private val _opcodes = dexFile.opcodes

    override fun getClasses(): Set<ClassDef> {
        return _classes.toSet()
    }

    override fun getOpcodes(): Opcodes {
        return _opcodes
    }

    /**
     * Adds a [ClassDef] if another [ClassDef] with the same [ClassDef.getType] doesn't exist,
     * in that case [classDefToAdd] replaces it.
     */
    fun addClassDef(
        classDefToAdd: ClassDef
    ) {
        val index = _classes.indexOfFirst { _classDef ->
            _classDef.type == classDefToAdd.type
        }

        if (index == -1) {
            _classes += classDefToAdd
        } else {
            _classes[index] = classDefToAdd
        }
    }

    /**
     * Finds a [ClassDef] by its descriptor. (i.e `Lsomepackage/someclass;`)
     *
     * Returns **null** if no match is found.
     */
    fun findClassDef(classDescriptor: String) =
        _classes.firstOrNull { it.type == classDescriptor }

}
