package com.example.multipleimagepicker.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.multipleimagepicker.R
import com.example.multipleimagepicker.databinding.ActivityMediaChooserBinding
import com.example.multipleimagepicker.helper.FileUtil
import com.example.multipleimagepicker.model.ImageItem
import java.io.IOException
import java.util.Locale
import kotlin.math.max

class MediaChooserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMediaChooserBinding
    private var type: String? = null
    private var imageAdapter: ImageAdapter? = null
    var mediaList: ArrayList<ImageItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaChooserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        mediaList = intent.getSerializableExtra("mediaList") as? ArrayList<ImageItem> ?: ArrayList()
        type = intent.getStringExtra("type")

        supportActionBar?.title = "Add ${type?.capitalize(Locale.getDefault())}s"

        if (type == "image") {
            galleryIntent()
        } else {
            galleryVideoIntent()
        }

        imageAdapter = ImageAdapter(mediaList, type)
        binding.mediaPager.adapter = imageAdapter
    }

    private fun galleryIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(Intent.createChooser(intent, "Select File"), 100)
    }

    private fun galleryVideoIntent() {
        val pickVideoIntent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Video.Media.EXTERNAL_CONTENT_URI).apply {
            type = "video/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(pickVideoIntent, 200)
    }

    private fun afterFileSelection(uri: Uri?, requestCode: Int) {
        if (uri == null) {
            Toast.makeText(this, "No media selected", Toast.LENGTH_SHORT).show()
            if (mediaList.isEmpty()) finish()
        } else {
            try {
                val destination = FileUtil.from(this, uri)
                val maxSizeMB = if (type == "video") 25 else Long.MAX_VALUE
                if (destination.length() / 1024 / 1024 > maxSizeMB) {
                    Toast.makeText(this, "${type?.capitalize(Locale.getDefault())} size too large, please select another $type", Toast.LENGTH_SHORT).show()
                    if (mediaList.isEmpty()) finish()
                } else {
                    val imageItem = ImageItem().apply {
                        type = 0
                        file = destination
                        image = Uri.fromFile(destination)
                    }

                    mediaList.add(imageItem)
                    imageAdapter?.notifyDataSetChanged()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.media_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.done -> {
                val intent = Intent().apply {
                    putExtra("mediaList", mediaList)
                }
                setResult(RESULT_OK, intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            val clipData = data.clipData
            if (clipData != null) {
                val maxCount = if (type == "image") 5 else 2
                val selectedCount = clipData.itemCount
                val retrieveCount = max(0, maxCount - mediaList.size)
                if (selectedCount > retrieveCount) {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "You can select only $retrieveCount ${type}s", Toast.LENGTH_LONG).show()
                    }
                    finish()
                }
                for (i in 0 until selectedCount) {
                    val uri = clipData.getItemAt(i).uri
                    if (!isMediaItemAlreadyAdded(uri)) {
                        afterFileSelection(uri, requestCode)
                    }
                }
            } else {
                val uri = data.data
                if (!isMediaItemAlreadyAdded(uri)) {
                    afterFileSelection(uri, requestCode)
                }
            }
        } else {
            finish()
        }
    }

    private fun isMediaItemAlreadyAdded(uri: Uri?): Boolean {
        val selectedPath = uri?.path
        for (mediaItem in mediaList) {
            val itemPath = mediaItem.image?.path
            if (itemPath != null && itemPath == selectedPath) {
                return true
            }
        }
        return false
    }

    inner class ImageAdapter(private val mediaList: ArrayList<ImageItem>, private val type: String?) :
        RecyclerView.Adapter<ImageAdapter.Holder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.media_selected_item, parent, false)
            return Holder(view)
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val mediaItem = mediaList[position]
            if (type == "image") {
                Glide.with(holder.itemView.context)
                    .load(mediaItem.image)
                    .into(holder.previewImage)
                holder.playButton.setVisibility(View.GONE)

            } else {
                loadThumbnail(holder.previewImage, mediaItem.image!!)
                holder.playButton.setVisibility(View.VISIBLE)

            }
        }

        private fun loadThumbnail(imageView: ImageView, videoUri: Uri) {
            Glide.with(imageView.context)
                .asBitmap()
                .load(videoUri)
                .into(object : SimpleTarget<Bitmap?>() {

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        imageView.setImageResource(R.drawable.ic_add_image)
                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap?>?
                    ) {
                        imageView.setImageBitmap(resource)
                    }
                })
        }

        override fun getItemCount(): Int = mediaList.size

        inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val previewImage: ImageView = itemView.findViewById(R.id.previewImage)
            val playButton: ImageView = itemView.findViewById(R.id.playIcon)

        }
    }
}
