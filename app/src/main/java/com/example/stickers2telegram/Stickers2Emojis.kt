package com.example.stickers2telegram

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
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
import com.google.android.material.snackbar.Snackbar
import java.io.File


class Stickers2Emojis : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityStickers2EmojisBinding

    lateinit var stickerItems: List<StickerItem>
    lateinit var datasource : Datasource
    private val testStickerID = "1001001"

    private val CREATE_STICKER_PACK_ACTION = "org.telegram.messenger.CREATE_STICKER_PACK"
    private val CREATE_STICKER_PACK_EMOJIS_EXTRA = "STICKER_EMOJIS"
    private val CREATE_STICKER_PACK_IMPORTER_EXTRA = "IMPORTER"

    lateinit var workDir : File

    fun toggleFAB(){
        if (binding.fab.visibility != View.VISIBLE){ binding.fab.show() }
        else { binding.fab.hide() }

    }
    fun saveEmojis(){
        datasource.saveEmojis(stickerItems)
    }

    fun searchAvailableTelegram(): String {
        val pm = packageManager
        val tgList = listOf<String>(
            "org.telegram.messenger",
            "org.telegram.messenger.web",
            "org.thunderdog.challegram")

        for (pkg in tgList) {
            try {
                pm.getPackageInfo(pkg, PackageManager.GET_META_DATA)
                Log.e("F", pkg)
                return pkg
            } catch (e: PackageManager.NameNotFoundException) {
                continue
            }
        }
        Log.e("F", "")
        return ""
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

        datasource = Datasource(getExternalFilesDir(null)!!)

        val pkID = intent.getStringExtra("PKID")!!
        stickerItems = datasource.loadStickerItems(pkID)

        binding.fab.setOnClickListener { view ->
            val tgPKG = searchAvailableTelegram()
            if (tgPKG.isEmpty()) Snackbar.make(view, "Telegram not found", Snackbar.LENGTH_LONG).setAction("Action", null).show()
            else {doImport(tgPKG ,stickerItems)}
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
    private fun doImport(tgPKG: String ,stickersItems : List<StickerItem>) {

        val stickers = arrayListOf<Uri>()
        val emojis = arrayListOf<String>()

        val intent = Intent(CREATE_STICKER_PACK_ACTION)

        Log.e("SDK", Build.VERSION.SDK_INT.toString())
        stickersItems.forEach {
//            val uri = when (Build.VERSION.SDK_INT){
//                30 -> FileProvider.getUriForFile(this, "com.example.stickers2telegram.fileprovider", it.file)
//                else -> Uri.fromFile(it.file)
//            }
            if (it.emoji.isEmpty()) return@forEach
            val uri = FileProvider.getUriForFile(this, "com.example.stickers2telegram.fileprovider", it.file)
            this.grantUriPermission(tgPKG,uri,Intent.FLAG_GRANT_READ_URI_PERMISSION)
            stickers.add(uri)
            emojis.add(it.emoji)
        }

        intent.putExtra(Intent.EXTRA_STREAM, stickers) // (File)s
        intent.putExtra(CREATE_STICKER_PACK_IMPORTER_EXTRA, packageName)
        intent.putExtra(CREATE_STICKER_PACK_EMOJIS_EXTRA, emojis) // strings
        intent.type = "image/*"


        try {
            startActivity(intent)
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }
}