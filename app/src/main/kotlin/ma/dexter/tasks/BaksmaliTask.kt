package ma.dexter.tasks

import android.os.Handler
import android.os.Looper
import ma.dexter.managers.DexProjectManager
import ma.dexter.tools.smali.BaksmaliInvoker
import org.jf.dexlib2.dexbacked.DexBackedClassDef
import java.util.concurrent.Executors

object BaksmaliTask {

    fun execute(
        dexClassDef: DexBackedClassDef,
        callback: (smali: String) -> Unit
    ) {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            val smali = DexProjectManager.getSmaliModel(dexClassDef) // BaksmaliInvoker.disassemble(dexClassDef)

            handler.post {
                callback(smali.smaliCode)
            }
        }
    }

}
