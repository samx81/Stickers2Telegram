package com.example.stickers2telegram

import android.annotation.SuppressLint
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
import java.io.FileOutputStream
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*

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
    private lateinit var bitmap: Bitmap

    private lateinit var _stickerItem: StickerItem

    private fun saveImg(bitmap : Bitmap){
        val out = FileOutputStream(_stickerItem.file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        out.flush()
        out.close()
        Log.i("Saved","as title")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        (activity as Stickers2Emojis).toggleFAB()
        arguments?.let {
            _stickerItem = (activity as Stickers2Emojis).stickerItems[it.getInt(IDX)]
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)

        image = Glide.with(requireContext()).asBitmap().load(_stickerItem.file)
        image.override(512, 512).fitCenter().into(binding.editFragImage)

        viewLifecycleOwner.lifecycleScope.launch {
            bitmap = withContext(Dispatchers.IO) {
                image.submit().get()
            }
            binding.sizeText.text = "WxH:${bitmap.width}x${bitmap.height} (auto-scaled to 512)"
        }


        // TODO: Limited to emojis
        if (_stickerItem.emoji.isNotEmpty()){
            binding.editEmoji.setText(_stickerItem.emoji)
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            saveImg(bitmap)
            _stickerItem.emoji = binding.editEmoji.text.toString()
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as Stickers2Emojis).toggleFAB()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
    }
}