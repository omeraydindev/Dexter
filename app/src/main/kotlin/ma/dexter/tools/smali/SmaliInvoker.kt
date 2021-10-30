package ma.dexter.tools.smali

import ma.dexter.tasks.Error
import ma.dexter.tasks.Result
import ma.dexter.tools.smali.catcherr.smaliCatchErrFlexLexer
import ma.dexter.tools.smali.catcherr.smaliCatchErrParser
import ma.dexter.tools.smali.catcherr.smaliCatchErrTreeWalker
import org.antlr.runtime.CommonTokenStream
import org.antlr.runtime.tree.CommonTreeNodeStream
import org.jf.dexlib2.Opcodes
import org.jf.dexlib2.iface.ClassDef
import org.jf.dexlib2.writer.builder.DexBuilder
import org.jf.smali.SmaliOptions
import java.io.StringReader

object SmaliInvoker {

    /**
     * Assembles given [smaliCode] into a [ClassDef].
     */
    fun assemble(
        smaliCode: String,
        options: SmaliOptions = SmaliOptions()
    ): Result<ClassDef> {
        return try {
            assembleInternal(smaliCode, options)
        } catch (e: Exception) { // catch everything, will be shown to the user anyway
            Result(success = false, error = Error.fromException(e))
        }
    }

    private fun assembleInternal(
        smaliCode: String,
        options: SmaliOptions
    ): Result<ClassDef> {

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
            return Result(
                success = false,
                error = Error("Lexer", lexer.getErrorsString())
            )
        }

        if (parser.numberOfSyntaxErrors > 0) {
            return Result(
                success = false,
                error = Error("Parser", parser.getErrorsString())
            )
        }

        val treeStream = CommonTreeNodeStream(result.tree)
        treeStream.tokenStream = tokens

        val treeWalker = smaliCatchErrTreeWalker(treeStream).apply {
            setApiLevel(options.apiLevel)
            setVerboseErrors(options.verboseErrors)
            setDexBuilder(dexBuilder)
        }
        val classDef = treeWalker.smali_file()

        if (treeWalker.numberOfSyntaxErrors > 0) {
            return Result(
                success = false,
                error = Error("Tree walker", treeWalker.getErrorsString())
            )
        }

        return Result(classDef, true)
    }

}
