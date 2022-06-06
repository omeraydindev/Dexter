package ma.dexter.tools.smali

import ma.dexter.api.consumer.ClassProgressConsumer
import ma.dexter.dex.MutableClassDef
import ma.dexter.util.normalizeSmaliPath
import org.jf.baksmali.Adaptors.ClassDefinition
import org.jf.baksmali.BaksmaliOptions
import org.jf.baksmali.formatter.BaksmaliWriter
import org.jf.dexlib2.DexFileFactory
import org.jf.dexlib2.iface.ClassDef
import java.io.File
import java.io.StringWriter
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class BaksmaliInvoker(
    private val baksmaliOptions: BaksmaliOptions = BaksmaliOptions(),
) {

    fun disassemble(
        classDef: MutableClassDef
    ): String {
        return disassemble(classDef.classDef)
    }

    private fun disassemble(
        classDef: ClassDef
    ): String {
        val writer = StringWriter()
        val classDefinition = ClassDefinition(baksmaliOptions, classDef)
        classDefinition.writeTo(BaksmaliWriter(writer))
        return writer.toString()
    }

    fun disassemble(
        dexFile: File,
        outZip: File,
        progressConsumer: ClassProgressConsumer,
    ) {
        val classDefs = DexFileFactory.loadDexFile(dexFile, null)
            .classes.toSet().filterNotNull()

        val currentClassCount = AtomicInteger()
        val totalClassCount = classDefs.size

        ZipOutputStream(outZip.outputStream()).use { zos ->
            classDefs.forEach { classDef ->
                val smaliPath = normalizeSmaliPath(classDef.type)
                progressConsumer.consume(smaliPath, currentClassCount.incrementAndGet(), totalClassCount)

                val zipEntry = ZipEntry("$smaliPath.smali")
                zos.putNextEntry(zipEntry)
                zos.write(disassemble(classDef).toByteArray())
                zos.closeEntry()
            }
        }
    }

}
