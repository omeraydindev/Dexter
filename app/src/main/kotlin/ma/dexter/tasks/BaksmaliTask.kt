package ma.dexter.tasks

import android.os.Handler
import android.os.Looper
import ma.dexter.project.DexProjectManager
import org.jf.dexlib2.iface.ClassDef
import java.util.concurrent.Executors

object BaksmaliTask {

    fun execute(
        classDef: ClassDef,
        callback: (smali: String) -> Unit
    ) {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            val smali = DexProjectManager.getSmaliModel(classDef) // BaksmaliInvoker.disassemble(classDef)

            handler.post {
                callback(smali.smaliCode)
            }
        }
    }

}
