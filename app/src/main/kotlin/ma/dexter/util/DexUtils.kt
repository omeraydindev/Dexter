package ma.dexter.util

fun isValidDexFileName(
    fileName: String
): Boolean {
    return fileName.startsWith("classes") && fileName.endsWith(".dex")
}
