package com.example.multipleimagepicker.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.multipleimagepicker.R
import com.example.multipleimagepicker.databinding.ActivityMainBinding
import com.example.multipleimagepicker.helper.Utility
import com.example.multipleimagepicker.model.ImageItem
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var imageItemArrayList: ArrayList<ImageItem> = ArrayList()
    private var videoItemArrayList: ArrayList<ImageItem> = ArrayList()
    var fileImage1: File? = null
    var fileImage2: File? = null
    var fileImage3: File? = null
    var fileImage4: File? = null
    var fileImage5: File? = null
    var fileVideo1: File? = null
    var fileVideo2: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()

    }

    private fun setListeners() {
        binding.addImage.setOnClickListener {
            val _isGrant: Boolean
            _isGrant = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Utility.checkStoragePermission(this)
            } else {
                Utility.checkPermission(this)
            }
            if (_isGrant) {
                val i =
                    Intent(this, MediaChooserActivity::class.java)
                i.putExtra("type", "image")
                i.putExtra("mediaList", imageItemArrayList)
                startActivityForResult(i, 100)
            }
        }

        binding.addVideo.setOnClickListener {
            val _isGrant: Boolean
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                _isGrant = Utility.checkStoragePermission(this)
            } else {
                _isGrant = Utility.checkPermission(this)
            }
            if (_isGrant) {
                val i =
                    Intent(this, MediaChooserActivity::class.java)
                i.putExtra("type", "video")
                i.putExtra("mediaList", videoItemArrayList)
                startActivityForResult(i, 200)
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 100) {
                val receivedMediaList =
                    data.getSerializableExtra("mediaList") as java.util.ArrayList<ImageItem>?
                handleReceivedMediaList(receivedMediaList!!)
            } else if (requestCode == 200) {
                val receivedVideoList =
                    data.getSerializableExtra("mediaList") as java.util.ArrayList<ImageItem>?
                handleReceivedVideoList(receivedVideoList!!)
            }
        }
    }

    private fun handleReceivedMediaList(receivedMediaList: java.util.ArrayList<ImageItem>) {
        imageItemArrayList.clear()
        imageItemArrayList.addAll(receivedMediaList)
        for (i in receivedMediaList.indices) {
            val imageItem = receivedMediaList[i]
            if (i == 0) {
                fileImage1 = imageItem.file
            } else if (i == 1 && imageItem.file != null) {
                fileImage2 = imageItem.file
            } else if (i == 2 && imageItem.file != null) {
                fileImage3 = imageItem.file
            } else if (i == 3 && imageItem.file != null) {
                fileImage4 = imageItem.file
            } else if (i == 4 && imageItem.file != null) {
                fileImage5 = imageItem.file
            }
        }
           if (!receivedMediaList.isEmpty()) {
            val addImageCardView = findViewById<CardView>(R.id.addImage)
            addImageCardView.background = ContextCompat.getDrawable(this, R.drawable.card_uploaded_border)
        } else {
            val addImageCardView = findViewById<CardView>(R.id.addImage)
            addImageCardView.background =
                ContextCompat.getDrawable(this, R.drawable.card_white_border)
        }
    }

    private fun handleReceivedVideoList(receivedVideoList: java.util.ArrayList<ImageItem>) {
        videoItemArrayList.clear()
        videoItemArrayList.addAll(receivedVideoList)
        for (i in receivedVideoList.indices) {
            val videoItem = receivedVideoList[i]
            if (i == 0) {
                fileVideo1 = videoItem.file
            } else if (i == 1 && videoItem.file != null) {
                fileVideo2 = videoItem.file
            }
        }


        if (!receivedVideoList.isEmpty()) {
            val addImageCardView = findViewById<CardView>(R.id.addVideo)
            addImageCardView.background = ContextCompat.getDrawable(this, R.drawable.card_uploaded_border)
        } else {
            val addImageCardView = findViewById<CardView>(R.id.addVideo)
            addImageCardView.background =
                ContextCompat.getDrawable(this, R.drawable.card_white_border)
        }
    }

}