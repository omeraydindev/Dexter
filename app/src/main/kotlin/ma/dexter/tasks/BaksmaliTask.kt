package ma.dexter.tasks

import android.os.Handler
import android.os.Looper
import ma.dexter.dex.MutableClassDef
import ma.dexter.project.DexProject
import java.util.concurrent.Executors

object BaksmaliTask {

    fun execute(
        classDef: MutableClassDef,
        callback: (smali: String) -> Unit
    ) {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            val smali = DexProject.getOpenedProject()
                .smaliContainer.getSmaliCode(classDef)

            handler.post {
                callback(smali)
            }
        }
    }

}
