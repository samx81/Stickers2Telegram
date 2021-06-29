package com.example.stickers2telegram

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.stickers2telegram.data.Datasource
import com.example.stickers2telegram.databinding.ActivityStickers2EmojisBinding
import com.example.stickers2telegram.model.StickerItem
import java.io.File

class Stickers2Emojis : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityStickers2EmojisBinding

    lateinit var stickerItems: List<StickerItem>
    private val testStickerID = "1001001"

    private val CREATE_STICKER_PACK_ACTION = "org.telegram.messenger.CREATE_STICKER_PACK"
    private val CREATE_STICKER_PACK_EMOJIS_EXTRA = "STICKER_EMOJIS"
    private val CREATE_STICKER_PACK_IMPORTER_EXTRA = "IMPORTER"

    lateinit var workDir : File

    fun toggleFAB(){
        if (binding.fab.visibility != View.VISIBLE){ binding.fab.show() }
        else { binding.fab.hide() }

    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        workDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS),
            "stk2tg"
        )

        binding = ActivityStickers2EmojisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_stickers2_emojis)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        val datasource = Datasource(workDir)

        val pkID = intent.getStringExtra("PKID")!!
        stickerItems = datasource.loadStickerItems(pkID)

        binding.fab.setOnClickListener { view ->
            doImport(stickerItems)
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_stickers2_emojis)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun doImport(stickersItems : List<StickerItem>) {
        Log.e("test", workDir.toString())
        Log.e("test", applicationContext.getExternalFilesDir(null)?.absolutePath.toString())
        val stickers = arrayListOf<Uri>()
        val emojis = arrayListOf<String>()
        stickersItems.forEach {
            val uri = Uri.fromFile(it.file)
            Log.wtf("WTF",uri.toString())
            stickers.add(uri)
            emojis.add(it.emoji)
        }

        val intent = Intent(CREATE_STICKER_PACK_ACTION)
        intent.putExtra(Intent.EXTRA_STREAM, stickers) // (File)s
        intent.putExtra(CREATE_STICKER_PACK_IMPORTER_EXTRA, packageName)
        intent.putExtra(CREATE_STICKER_PACK_EMOJIS_EXTRA, emojis) // strings
        intent.type = "image/*"
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        try {
            startActivity(intent)
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }
}