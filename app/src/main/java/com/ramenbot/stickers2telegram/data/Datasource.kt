package com.ramenbot.stickers2telegram.data

import android.util.Log
import com.ramenbot.stickers2telegram.model.StickerItem
import java.io.File


class Datasource (private val workDir: File) {

    val regex = Regex("\\d+@2x.png")
    lateinit var emojiListFile : File

    fun unzipPack(stickerID: String){
        val f = File(workDir, "$stickerID.zip")
        val destDir = File(workDir, stickerID)
        UnzipUtils.unzip(f, destDir.path)
    }

    fun getEmojiMap(stickerDir : File): MutableMap<String,String>{
        emojiListFile = File(stickerDir, "emoji.txt")
        val emojiMap = mutableMapOf<String, String>()
        if (!emojiListFile.isFile) {
            emojiListFile.createNewFile()
        }
        val emojilst = emojiListFile.readText()
        // Pair of filename to emoji
        if (emojilst.isNotEmpty()){
            emojilst.split("\n")
                .filter { it.isNotEmpty() } // I don't know why but filtering is working
                .associateTo(emojiMap) {
                    it.split(",").let { (k, v) -> k to v }
                }
        }
        return emojiMap
    }

    fun saveEmojis(items : List<StickerItem>){

        var outputstr = ""
        items.filter { it.emoji.isNotEmpty() }.forEach {
            outputstr += "${it.file.name},${it.emoji}\n"
        }
        Log.e("test", outputstr)
        emojiListFile.writeText(outputstr)

    }

    fun loadStickerItems(stickerID: String) : List<StickerItem>{
        val stickerItems = mutableListOf<StickerItem>()
        val stickerDir = File(workDir, stickerID)

        if (!stickerDir.isDirectory){
            unzipPack(stickerID)
        }
        val map = getEmojiMap(stickerDir)

        stickerDir.listFiles()!!.sorted().forEach {
            if ( it.name.contains(regex) ) stickerItems.add(StickerItem(it, map.getOrDefault(it.name, "")))
        }
        return stickerItems.toList()
    }
}