package ma.dexter.util

import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.iface.ClassDef

fun isValidDexFileName(
    fileName: String
): Boolean {
    return fileName.startsWith("classes") && fileName.endsWith(".dex")
}

fun ClassDef.isInterface(): Boolean {
    return (this.accessFlags and AccessFlags.INTERFACE.value) != 0
}

fun ClassDef.isEnum(): Boolean {
    return (this.accessFlags and AccessFlags.ENUM.value) != 0
}

fun ClassDef.isAnnotation(): Boolean {
    return (this.accessFlags and AccessFlags.ANNOTATION.value) != 0
}
