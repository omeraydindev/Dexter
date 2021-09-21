package ma.dexter.tools.smali

import android.util.Log
import ma.dexter.tools.smali.catcherr.smaliCatchErrFlexLexer
import ma.dexter.tools.smali.catcherr.smaliCatchErrParser
import ma.dexter.tools.smali.catcherr.smaliCatchErrTreeWalker
import org.antlr.runtime.CommonTokenStream
import org.antlr.runtime.tree.CommonTreeNodeStream
import org.jf.dexlib2.Opcodes
import org.jf.dexlib2.writer.builder.DexBuilder
import org.jf.dexlib2.writer.io.FileDataStore
import org.jf.smali.*
import java.io.File
import java.io.StringReader
import java.lang.Exception

object SmaliInvoker {

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
    ): Result {

        return try {
            assemble_(smaliCode, outputDex, options)
        } catch (e: Exception) { // catch everything, will be shown to the user anyway
            Result(false, Log.getStackTraceString(e))
        }

    }

    private fun assemble_(
        smaliCode: String,
        outputDex: File,
        options: SmaliOptions
    ): Result {

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
            return Result(false, "Lexer:\n" + lexer.getErrors())
        }

        if (parser.numberOfSyntaxErrors > 0) {
            return Result(false, "Parser:\n" + parser.getErrors())
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
            return Result(false, "Tree walker:\n" + dexGen.getErrors())
        }

        dexBuilder.writeTo(FileDataStore(outputDex))

        return Result(true, "")
    }

    class Result(val success: Boolean, val error: String)

}
