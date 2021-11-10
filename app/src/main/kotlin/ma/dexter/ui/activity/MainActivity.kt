package ma.dexter.ui.activity

import android.Manifest
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import com.github.angads25.filepicker.model.DialogConfigs
import com.github.angads25.filepicker.model.DialogProperties
import com.github.angads25.filepicker.view.FilePickerDialog
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ma.dexter.R
import ma.dexter.databinding.ActivityMainBinding
import ma.dexter.dex.DexFactory
import ma.dexter.project.DexProject
import ma.dexter.tasks.MergeDexTask
import ma.dexter.tasks.SaveDexTask
import ma.dexter.tasks.runWithDialog
import ma.dexter.ui.BaseActivity
import ma.dexter.ui.adapter.DexPagerAdapter
import ma.dexter.ui.viewmodel.MainViewModel
import ma.dexter.util.*
import java.io.File

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()
    private lateinit var pagerAdapter: DexPagerAdapter

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

        initLiveData()
        initTabs()
    }

    override fun onBackPressed() {
        if (binding.viewPager.currentItem != 0) {
            binding.viewPager.currentItem = 0
        } else {
            MaterialAlertDialogBuilder(this)
                .setTitle("Warning")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("YES") { _, _ ->
                    super.onBackPressed()
                }
                .setNegativeButton("NO", null)
                .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (binding.viewPager.currentItem != 0) return false

        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (binding.viewPager.currentItem != 0) return false

        when (item.itemId) {
            R.id.it_more -> {
                popupMenu {
                    section {
                        title = "DEX"
                        item {
                            label = "Open..."
                            callback = ::openDexFiles
                        }
                        item {
                            label = "Save"
                            callback = ::saveDexFiles
                        }
                    }
                    section {
                        title = "Tools"
                        item {
                            label = "DEX Merger"
                            callback = ::mergeDexFiles
                        }
                    }
                    section {
                        title = "About"
                        item {
                            label = "GitHub"
                            callback = {
                                openUrl(this@MainActivity, "https://github.com/MikeAndrson/Dexter")
                            }
                        }
                    }
                }.show(this, findViewById(R.id.it_more))
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun openDexFiles() {
        val properties = DialogProperties().apply {
            root = storagePath
            extensions = arrayOf("dex")
            selection_mode = DialogConfigs.MULTI_MODE
        }

        FilePickerDialog(this, properties).run {
            setTitle("Select DEX file(s)")
            setDialogSelectionListener { dexPaths ->
                viewModel.removeAllPageItems()

                viewModel.dexProject.value =
                    DexProject(dexPaths.map { path ->
                        DexFactory.fromFile(File(path))
                    })
            }
            show()
        }
    }

    private fun saveDexFiles() {
        SaveDexTask()
            .runWithDialog(this, "Saving DEX files", "") {
                if (it.value != null) {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Saved successfully")
                        .setMessage(it.value)
                        .show()
                }
            }
    }

    private fun mergeDexFiles() {
        val properties = DialogProperties().apply {
            root = storagePath
            extensions = arrayOf("dex")
            selection_mode = DialogConfigs.MULTI_MODE
        }

        FilePickerDialog(this, properties).run {
            setTitle("Select DEX files to merge")
            setDialogSelectionListener { paths ->
                val mergedDexFile = File(File(paths[0]).parent, "classes_MERGED.dex")

                MergeDexTask(paths, mergedDexFile)
                    .runWithDialog(this@MainActivity, "Merging DEX files", "") {
                        if (it.value != null) {
                            MaterialAlertDialogBuilder(this@MainActivity)
                                .setTitle("Merged successfully")
                                .setMessage(it.value)
                                .show()
                        }
                    }
            }
            show()
        }
    }

    private fun initLiveData() {
        viewModel.getPageItems().observe(this) {
            pagerAdapter.updateList(it)
        }

        viewModel.currentPosition.observe(this) {
            binding.viewPager.currentItem = it
            hideKeyboard(binding.viewPager)
        }
    }

    private fun initTabs() {
        pagerAdapter = DexPagerAdapter(this)

        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.offscreenPageLimit = 100 // todo
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                viewModel.viewPagerScrolled.value = positionOffsetPixels
            }

            override fun onPageSelected(pos: Int) {
                invalidateOptionsMenu()
            }
        })
        binding.viewPager.adapter = pagerAdapter

        viewModel.addMainItem()

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            tab.text = pagerAdapter.getItem(pos).getTitle()
            tab.setIcon(pagerAdapter.getItem(pos).getIconResId())
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab) {
                // position 0 is reserved for DexEditorFragment (for now)
                if (tab.position == 0) return

                popupMenu {
                    section {
                        item {
                            label = "Close"
                            callback = {
                                viewModel.removePageItem(tab.position)
                            }
                        }
                        item {
                            label = "Close others"
                            callback = {
                                viewModel.removeAllPageItems(excludePos = tab.position)
                            }
                        }
                        item {
                            label = "Close all"
                            callback = {
                                viewModel.removeAllPageItems()
                            }
                        }
                    }
                }.show(this@MainActivity, tab.view)
            }
        })
    }

}
