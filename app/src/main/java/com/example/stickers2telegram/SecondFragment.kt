package com.example.stickers2telegram

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.example.stickers2telegram.databinding.FragmentSecondBinding
import com.example.stickers2telegram.model.StickerItem
import kotlinx.coroutines.runBlocking
import java.io.FileOutputStream
import android.widget.ProgressBar
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    companion object {
        val IDX = "idx"
    }

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var image : RequestBuilder<Bitmap>

    private lateinit var _stickerItem: StickerItem

    fun saveImg(bitmap : Bitmap){
        val out = FileOutputStream(_stickerItem.file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        out.flush()
        out.close()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            _stickerItem = (activity as Stickers2Emojis).stickerItems[it.getInt(IDX)]
        }
    }

//    suspend fun loadImage() {
//        coroutineScope {
//
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)

        image = Glide.with(requireContext()).asBitmap().load(_stickerItem.file).override(512, 512)
        var bitmap : Bitmap
        image.fitCenter()
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any?,
                    target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    Log.e("Error",e.toString())
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    bitmap = resource!!
                    return true
                }
            }).into(binding.editFragImage)

        binding.sizeText.text = "${bitmap.height}x${bitmap.width} (After Scaled to 512)"

        // TODO: Limited to emojis
        if (_stickerItem.emoji.isNotEmpty()){
            binding.editEmoji.setText(_stickerItem.emoji)
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}