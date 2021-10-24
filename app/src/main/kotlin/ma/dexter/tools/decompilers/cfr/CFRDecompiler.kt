package ma.dexter.tools.decompilers.cfr

import ma.dexter.tools.decompilers.BaseJarDecompiler
import org.benf.cfr.reader.api.CfrDriver
import java.io.File
import java.util.jar.JarFile

class CFRDecompiler : BaseJarDecompiler {
    private val options = defaultOptions()

    /*
     * Adapted from https://github.com/leibnitz27/cfr_client/blob/master/src/org/benf/cfr_client_example/WithBetterSink.java
     */
    override fun decompileJar(
        className: String,
        jarFile: File
    ): String {
        val jar = JarFile(jarFile)

        val outputSink = CFROutputSink()

        val jarSource = CFRJarSource(jar)

        val driver = CfrDriver.Builder()
            .withClassFileSource(jarSource)
            .withOutputSink(outputSink)
            .withOptions(options)
            .build()

        driver.analyse(listOf(className))
        return outputSink.javaCode
    }

    // CFR already appends a banner by default
    override fun getBanner(): String? = null

    override fun getName() = "CFR"

    private fun defaultOptions() = mapOf(
        "comments" to "false" // Disables 'Cannot load following classes' comments
    )
}
