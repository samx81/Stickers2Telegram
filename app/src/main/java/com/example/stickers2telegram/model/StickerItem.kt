package com.example.stickers2telegram.model

import java.io.File

data class StickerItem (val file : File, var emoji : String = "") {
    // TODO("can I modify attr on the fly?")
}