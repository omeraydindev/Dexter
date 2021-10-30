package ma.dexter.util

import ma.dexter.App
import java.io.File
import java.io.FileOutputStream

/**
 * Extracts an asset to the given [destinationFile].
 *
 * Silently returns if [destinationFile] already exists.
 */
fun extractAsset(
    assetFileName: String,
    destinationFile: File
) {
    if (destinationFile.exists()) return

    val input = App.context.assets.open(assetFileName)
    val output = FileOutputStream(destinationFile)

    input.use {
        output.use {
            input.copyTo(output)
        }
    }
}
