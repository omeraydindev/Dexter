package ma.dexter.tools.decompilers.cfr

import org.benf.cfr.reader.api.OutputSinkFactory
import org.benf.cfr.reader.api.SinkReturns
import java.util.*

class CFROutputSink : OutputSinkFactory {
    private val _javaCode = StringBuilder()

    val javaCode: String
        get() = _javaCode.toString()

    override fun getSupportedSinks(
        sinkType: OutputSinkFactory.SinkType,
        collection: Collection<OutputSinkFactory.SinkClass>
    ): List<OutputSinkFactory.SinkClass> {

        return if (sinkType == OutputSinkFactory.SinkType.JAVA && OutputSinkFactory.SinkClass.DECOMPILED in collection) {
            listOf(
                OutputSinkFactory.SinkClass.DECOMPILED,
                OutputSinkFactory.SinkClass.STRING
            )
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
                    _javaCode.append(java)
                }
            }
        }

        return OutputSinkFactory.Sink {}
    }

}
