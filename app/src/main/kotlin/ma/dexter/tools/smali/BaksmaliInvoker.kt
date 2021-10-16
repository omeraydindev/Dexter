package ma.dexter.tools.smali

import org.jf.baksmali.Adaptors.ClassDefinition
import org.jf.baksmali.BaksmaliOptions
import org.jf.baksmali.formatter.BaksmaliWriter
import org.jf.dexlib2.iface.ClassDef
import java.io.StringWriter

object BaksmaliInvoker {

    fun disassemble(
        dexClassDef: ClassDef,
        baksmaliOptions: BaksmaliOptions = BaksmaliOptions()
    ): String {
        val writer = StringWriter()

        val classDefinition = ClassDefinition(baksmaliOptions, dexClassDef)
        classDefinition.writeTo(BaksmaliWriter(writer, null))

        return writer.toString()
    }

}
