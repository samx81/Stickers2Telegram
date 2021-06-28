package com.example.stickers2telegram.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stickers2telegram.FirstFragmentDirections
import com.example.stickers2telegram.R
import com.example.stickers2telegram.model.StickerItem
import kotlin.random.Random

class ItemAdapter(private val context: Context, private val dataset: List<StickerItem>
    ): RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {


    private val progressBar = ProgressBar(context)

    class ItemViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        // TODO: change later to fit into images
        val button : CardView = view.findViewById(R.id.item_card)
        val textView: TextView = view.findViewById(R.id.item_title)
        val imageView: ImageView = view.findViewById(R.id.item_image)
        val emojiView: TextView = view.findViewById(R.id.item_emoji)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        progressBar.layoutParams = lp
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        // TODO: change this assign method
        holder.button.setOnClickListener {
            val action = FirstFragmentDirections.actionFirstFragmentToSecondFragment(idx = position)
            it.findNavController().navigate(action)
        }
        Glide.with(context).load(item.file).placeholder(progressBar.indeterminateDrawable).into(holder.imageView)
        holder.textView.text = item.file.nameWithoutExtension
        holder.emojiView.text = item.emoji
    }

    override fun getItemCount() = dataset.size
}