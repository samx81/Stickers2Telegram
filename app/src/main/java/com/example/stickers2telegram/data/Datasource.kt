package com.example.stickers2telegram.data

import com.example.stickers2telegram.model.StickerItem

class Datasource {
    fun loadStickerItem() : List<StickerItem>{
        return listOf<StickerItem>(StickerItem(123))
    }
}