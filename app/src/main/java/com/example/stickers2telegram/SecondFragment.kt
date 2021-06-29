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
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import java.util.*
import java.util.regex.Pattern
import kotlin.streams.toList

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


        // TODO: Limited to emojis, ( right now only blocking alphabet & number )
        if (_stickerItem.emoji.isNotEmpty()){
            binding.editEmoji.setText(_stickerItem.emoji)
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            val p = Pattern.compile("\\w|\\d")
            val inputemoji = binding.editEmoji.text.toString()


            for (i in inputemoji.splitToCodePoints()){
                if (p.matcher(i).matches()) {
                    Snackbar.make(it,"有不屬於表符的輸入，請更正", Snackbar.LENGTH_LONG).setAction("Action", null).show()
                    return@setOnClickListener
                }
            }

            saveImg(bitmap)
            _stickerItem.emoji = inputemoji
            (activity as Stickers2Emojis).saveEmojis()
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }
    // https://stackoverflow.com/questions/63173425/map-string-with-emojis-to-arraystringchar
    fun String.splitToCodePoints(): List<String> {
        return codePoints()
            .toList()
            .map { String(Character.toChars(it)) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as Stickers2Emojis).toggleFAB()
        _binding = null
    }
}