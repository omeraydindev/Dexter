package ma.dexter.ui.activity

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.github.angads25.filepicker.model.DialogConfigs
import com.github.angads25.filepicker.model.DialogProperties
import com.github.angads25.filepicker.view.FilePickerDialog
import ma.dexter.R
import ma.dexter.databinding.ActivityMainBinding
import ma.dexter.managers.DexProjectManager
import ma.dexter.ui.fragment.DexEditorFragment
import ma.dexter.util.requestAllFilesAccessPermission
import ma.dexter.util.storagePath
import org.jf.dexlib2.dexbacked.DexBackedClassDef

class MainActivity : BaseActivity(), DexEditorFragment.DexItemClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dexEditorFragment: DexEditorFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), 100
        )

        requestAllFilesAccessPermission(this)

        subtitle = "by MikeAndrson"
        init()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.it_github -> {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/MikeAndrson/Dexter")
                    )
                )
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun init() {
        supportFragmentManager.beginTransaction().run {
            dexEditorFragment = DexEditorFragment()

            replace(R.id.container, dexEditorFragment, DexEditorFragment::class.simpleName)
            commit()
        }

        binding.btnLoadDex.setOnClickListener {
            val props = DialogProperties().apply {
                root = storagePath
                extensions = arrayOf("dex", "apk")
                selection_mode = DialogConfigs.MULTI_MODE
            }

            FilePickerDialog(this, props).run {
                setTitle("Select .dex file(s) or an .apk file")
                setDialogSelectionListener {

                    if (it[0].endsWith(".apk")) {
                        dexEditorFragment.loadApk(it[0])
                    } else {
                        dexEditorFragment.loadDexes(it)
                    }

                }
                show()
            }
        }
    }

    override fun onDexClassItemClick(dexClassDef: DexBackedClassDef) {
        DexProjectManager.gotoClassDef(this, dexClassDef)
    }

}
