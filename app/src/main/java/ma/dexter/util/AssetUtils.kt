package ma.dexter.util

import ma.dexter.App
import java.io.File
import java.io.FileOutputStream

/**
 * Extracts an asset to the given [destinationFile].
 *
 * Doesn't do anything if [destinationFile] already exists.
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
            val buffer = ByteArray(input.available())
            var length: Int

            while (input.read(buffer).also { length = it } != -1) {
                output.write(buffer, 0, length)
            }
        }
    }
}
