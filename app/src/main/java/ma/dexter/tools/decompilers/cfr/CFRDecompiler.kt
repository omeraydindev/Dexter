package ma.dexter.tools.decompilers.cfr

import ma.dexter.tools.decompilers.BaseJarDecompiler
import org.benf.cfr.reader.api.CfrDriver
import org.benf.cfr.reader.api.OutputSinkFactory
import org.benf.cfr.reader.api.SinkReturns
import java.io.File
import java.lang.StringBuilder
import java.util.*

class CFRDecompiler : BaseJarDecompiler {
    private val options = defaultOptions()

    /**
     * Adapted from [https://github.com/leibnitz27/cfr_client/blob/master/src/org/benf/cfr_client_example/WithBetterSink.java]
     */
    override fun decompile(classFile: File): String {

        val javaCode = StringBuilder()

        val outSink = object : OutputSinkFactory {
            override fun getSupportedSinks(
                sinkType: OutputSinkFactory.SinkType,
                collection: Collection<OutputSinkFactory.SinkClass>
            ): List<OutputSinkFactory.SinkClass> {

                return if (sinkType == OutputSinkFactory.SinkType.JAVA && collection.contains(
                        OutputSinkFactory.SinkClass.DECOMPILED)) {
                    listOf(OutputSinkFactory.SinkClass.DECOMPILED, OutputSinkFactory.SinkClass.STRING)
                } else {
                    Collections.singletonList(OutputSinkFactory.SinkClass.STRING)
                }

            }

            override fun <T> getSink(
                sinkType: OutputSinkFactory.SinkType,
                sinkClass: OutputSinkFactory.SinkClass
            ): OutputSinkFactory.Sink<T> {

                if (sinkType == OutputSinkFactory.SinkType.JAVA && sinkClass == OutputSinkFactory.SinkClass.DECOMPILED) {
                    return OutputSinkFactory.Sink {
                        (it as SinkReturns.Decompiled).run {
                            javaCode.append(java)
                        }
                    }
                }

                return OutputSinkFactory.Sink {}
            }
        }

        val driver = CfrDriver.Builder()
            .withOutputSink(outSink)
            .withOptions(options)
            .build()

        driver.analyse(listOf(classFile.absolutePath))
        return javaCode.toString()
    }

    // CFR already appends a banner by default
    override fun getBanner(): String? = null

    override fun getName() = "CFR"

    private fun defaultOptions() = mapOf(
        "comments" to "false" // Disables 'Cannot load following classes' comments
    )
}
