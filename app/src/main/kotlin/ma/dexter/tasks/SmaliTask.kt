package ma.dexter.tasks

import android.os.Handler
import android.os.Looper
import ma.dexter.tools.smali.SmaliInvoker
import org.jf.dexlib2.iface.ClassDef
import java.util.concurrent.Executors

object SmaliTask {

    fun execute(
        smaliCode: String,
        callback: (SmaliInvoker.Result<ClassDef>) -> Unit
    ) {
        val handler = Handler(Looper.getMainLooper())

        Executors.newSingleThreadExecutor().execute {
            val classDef = SmaliInvoker.assemble(smaliCode)

            handler.post {
                callback(classDef)
            }
        }
    }

}
