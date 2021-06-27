package com.example.stickers2telegram

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import coil.load
import kotlinx.coroutines.*
import okhttp3.*
import okio.*
import org.jsoup.Jsoup
import java.io.File
import java.io.IOException
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {
    lateinit var alertBuilder : AlertDialog.Builder
    lateinit var progressDialog: AlertDialog
    var storeId : String = "0"
    private val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val retrieveBtn : Button = findViewById(R.id.retrieve_btn)
        retrieveBtn.setOnClickListener { retrieveUrl() }

        val buildPackBtn : Button = findViewById(R.id.build_pack_btn)
        buildPackBtn.setOnClickListener { showProgress() }
    }

    private fun showProgress(){
        progressDialog = getDialogProgressBar().create()
        progressDialog.setCanceledOnTouchOutside(false)
//        progressDialog.setCancelable()
        progressDialog.show()
        runBlocking (Dispatchers.IO){
            launch {
                progressDialog.cancel()
            }
            getStickerPack()
        }
    }

    suspend fun getStickerPack(){
//        if (!this::storeId.isInitialized) return
        Log.i("ID ava?", storeId)
        if (storeId == "0") return
        val packUrl = "http://dl.stickershop.line.naver.jp/products/0/0/1/$storeId/iphone/stickers@2x.zip"

        // https://stackoverflow.com/questions/25893030/download-binary-file-from-okhttp
        val request = Request.Builder()
            .url(packUrl)
            .build()
        val response = OkHttpClient().newCall(request).execute()
        val downloadedFile = File(cacheDir, "$storeId.zip")
        val sink: BufferedSink = downloadedFile.sink().buffer()
        sink.writeAll(response.body!!.source())
        sink.close()
    }

    fun getDialogProgressBar(): AlertDialog.Builder {
        if (!this::alertBuilder.isInitialized) {
            alertBuilder = AlertDialog.Builder(this)
            alertBuilder.setTitle("Loading...")
        }

        val progressBar = ProgressBar(this)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        progressBar.layoutParams = lp
        alertBuilder.setView(progressBar)
        return alertBuilder
    }

    private fun retrieveUrl(){

        fun matchPattern(input:String, pattern:String): String {
            if (input.isEmpty()) {
                Toast.makeText(this, "有些資料為空", Toast.LENGTH_LONG).show()
            }
            val p = Pattern.compile(pattern)
            val m = p.matcher(input)
            if (m.matches()) { return m.group(1)!! }
            Log.i("Empty",m.toString())
            return ""
        }

        val thumbImg : ImageView = findViewById(R.id.thumb_img)
        val urlEnter : EditText = findViewById(R.id.url_enter)
        val stickerTitle : TextView = findViewById(R.id.sticker_title)

        // Expecting pattern like : https://line.me/S/sticker/1001001/?lang=blahblah
        var url = urlEnter.text.toString()
        val stickerPKID = matchPattern(url, ".*line.me/S/sticker/([0-9]*)/.*")

        if (stickerPKID.isEmpty()) {
            Toast.makeText(this, "請檢查輸入的連結", Toast.LENGTH_LONG).show()
            return
        }
        url = "https://line.me/S/sticker/$stickerPKID"
        GlobalScope.launch {
            val html = Jsoup.connect(url).get()
            // Shiba Inu (Shiba-Dog) stamps – LINE貼圖 | LINE STORE, regex: "(.+)\\s–\\sLINE"
            var title = html.title()
            Log.i("Empty", title)
            title = matchPattern(title, "(.+)\\s–\\sLINE.*")
            Log.i("Empty", "ret $title")
            if (title.isEmpty()) {
                Log.i("Empty", title)
                return@launch
            }
            withContext(Dispatchers.Main) {
                Log.i("AVC", title)
                stickerTitle.text = title
            }
        }

        val thumbUrl = "https://stickershop.line-scdn.net/stickershop/v1/product/$stickerPKID/LINEStorePC/main.png"
        thumbImg.load(thumbUrl)

        storeId = stickerPKID
    }

//    <p class="mdCMN38Item01Ttl" data-test="sticker-name-title">Shiba Inu (Shiba-Dog) stamps</p>
}