package com.example.stickers2telegram

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.stickers2telegram.data.Datasource
import com.example.stickers2telegram.databinding.ActivityStickers2EmojisBinding
import com.example.stickers2telegram.model.StickerItem

class Stickers2Emojis : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityStickers2EmojisBinding

    lateinit var stickerItems: List<StickerItem>
    private val testStickerID = "1001001"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStickers2EmojisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_stickers2_emojis)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        val datasource = Datasource(cacheDir)
        stickerItems = datasource.loadStickerItems(testStickerID)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_stickers2_emojis)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}