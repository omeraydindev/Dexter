package ma.dexter.tools.smali

import android.util.Log
import ma.dexter.dex.DexFactory
import ma.dexter.tools.smali.catcherr.smaliCatchErrFlexLexer
import ma.dexter.tools.smali.catcherr.smaliCatchErrParser
import ma.dexter.tools.smali.catcherr.smaliCatchErrTreeWalker
import org.antlr.runtime.CommonTokenStream
import org.antlr.runtime.tree.CommonTreeNodeStream
import org.jf.dexlib2.Opcodes
import org.jf.dexlib2.iface.ClassDef
import org.jf.dexlib2.writer.builder.DexBuilder
import org.jf.dexlib2.writer.io.FileDataStore
import org.jf.dexlib2.writer.io.MemoryDataStore
import org.jf.smali.SmaliOptions
import java.io.File
import java.io.StringReader

object SmaliInvoker {

    /**
     * Assembles given [smaliCode] into a [ClassDef].
     */
    fun assemble(
        smaliCode: String,
        options: SmaliOptions = SmaliOptions()
    ): Result<ClassDef> {
        try {
            val result: Result<DexBuilder> = assembleInternal(smaliCode, options)

            if (!result.success) return Result(null, false, result.error)

            val memoryDataStore = MemoryDataStore()
            result.value?.writeTo(memoryDataStore)
            val dex = DexFactory.fromByteArray(memoryDataStore.buffer)
            return Result(dex.classes.first(), true)

        } catch (e: Exception) { // catch everything, will be shown to the user anyway
            return Result(null, false, Log.getStackTraceString(e))
        }
    }

    /**
     * Assembles given [smaliCode] into a dex file ([outputDex]).
     *
     * Adapted from [org.jf.smali.Smali.assembleSmaliFile].
     * This one catches errors as well.
     */
    fun assemble(
        smaliCode: String,
        outputDex: File,
        options: SmaliOptions = SmaliOptions()
    ): Result<File> {
        try {
            val result: Result<DexBuilder> = assembleInternal(smaliCode, options)

            if (!result.success) return Result(null, false, result.error)

            result.value?.writeTo(FileDataStore(outputDex))
            return Result(outputDex, true)

        } catch (e: Exception) { // catch everything, will be shown to the user anyway
            return Result(null, false, Log.getStackTraceString(e))
        }
    }

    private fun assembleInternal(
        smaliCode: String,
        options: SmaliOptions
    ): Result<DexBuilder> {

        val dexBuilder = DexBuilder(Opcodes.forApi(options.apiLevel))

        val lexer = smaliCatchErrFlexLexer(StringReader(smaliCode), options.apiLevel)
        val tokens = CommonTokenStream(lexer)

        val parser = smaliCatchErrParser(tokens).apply {
            setVerboseErrors(options.verboseErrors)
            setAllowOdex(options.allowOdexOpcodes)
            setApiLevel(options.apiLevel)
        }

        val result = parser.smali_file()

        if (lexer.numberOfSyntaxErrors > 0) {
            return Result(success = false, error = "Lexer:\n" + lexer.getErrors())
        }

        if (parser.numberOfSyntaxErrors > 0) {
            return Result(success = false, error = "Parser:\n" + parser.getErrors())
        }

        val treeStream = CommonTreeNodeStream(result.tree)
        treeStream.tokenStream = tokens

        val dexGen = smaliCatchErrTreeWalker(treeStream).apply {
            setApiLevel(options.apiLevel)
            setVerboseErrors(options.verboseErrors)
            setDexBuilder(dexBuilder)
            smali_file()
        }

        if (dexGen.numberOfSyntaxErrors > 0) {
            return Result(success = false, error = "Tree walker:\n" + dexGen.getErrors())
        }

        return Result(dexBuilder, true)
    }

    class Result<T>(
        val value: T? = null,
        val success: Boolean,
        val error: String = ""
    )

}
