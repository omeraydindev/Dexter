package ma.dexter.tools.smali

import ma.dexter.dex.MutableClassDef
import org.jf.baksmali.Adaptors.ClassDefinition
import org.jf.baksmali.BaksmaliOptions
import org.jf.baksmali.formatter.BaksmaliWriter
import java.io.StringWriter

object BaksmaliInvoker {

    fun disassemble(
        classDef: MutableClassDef,
        baksmaliOptions: BaksmaliOptions = BaksmaliOptions()
    ): String {
        val writer = StringWriter()

        val classDefinition = ClassDefinition(baksmaliOptions, classDef.classDef)
        classDefinition.writeTo(BaksmaliWriter(writer, null))

        return writer.toString()
    }

}
