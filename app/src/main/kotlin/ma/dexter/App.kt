package ma.dexter

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {
        lateinit var context: App
            private set
    }
}
