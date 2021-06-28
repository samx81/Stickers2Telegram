package com.example.stickers2telegram.data

import com.example.stickers2telegram.model.StickerItem
import java.io.File

class Datasource (private val workDir: File) {
    val regex = Regex("\\d+@2x.png")
    fun unzipPack(stickerID: String){
        val f = File(workDir, "$stickerID.zip")
        val destDir = File(workDir, stickerID)
        UnzipUtils.unzip(f, destDir.path)
    }

    fun loadStickerItems(stickerID: String) : List<StickerItem>{
        // TODO: came up a better way handling the list
        val stickerItems = mutableListOf<StickerItem>()
        val stickerDir = File(workDir, stickerID)

        if (!stickerDir.isDirectory){
            unzipPack(stickerID)
        }

        // TODO: Remove this test code
        val testemojis = listOf<String>("\uD83D\uDE00","\uD83D\uDE0D","\uD83D\uDE06","\uD83D\uDE05")
        stickerDir.listFiles()?.forEach {
            if ( it.name.contains(regex) ) stickerItems.add(StickerItem(it, testemojis.random()))
        }
        return stickerItems.toList()
    }
}