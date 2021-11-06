package ma.dexter.ui

import androidx.appcompat.app.AppCompatActivity

open class BaseActivity: AppCompatActivity() {

    var subtitle: CharSequence
        get() = supportActionBar?.subtitle ?: ""
        set(value) {
            supportActionBar?.subtitle = value
        }

}
