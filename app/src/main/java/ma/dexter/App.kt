package ma.dexter

import android.app.Application
import android.util.Log
import ma.dexter.util.listenForSystemLogs

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        context = this

        listenForSystemLogs {
            Log.d("System/err", it)
        }
    }

    companion object {
        lateinit var context: App
            private set
    }
}
