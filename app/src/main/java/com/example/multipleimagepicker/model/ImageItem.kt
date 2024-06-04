package com.example.multipleimagepicker.model

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import java.io.File
import java.util.Objects

class ImageItem() : Parcelable {
    var drawable: Drawable? = null
        private set
    private var _type = 0
    var type: Int
        get() = _type
        set(value) {
            _type = value
        }
    var _file: File? = null
    var file: File?
        get() = _file
        set(value) {
            _file = value
        }
    var _image: Uri? = null
    var image: Uri?
        get() = _image
        set(value) {
            _image = value
        }
    var isSelected = false

    constructor(parcel: Parcel) : this() {
        _type = parcel.readInt()
        _file = parcel.readSerializable() as File?
        _image = parcel.readParcelable(Uri::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(_type)
        parcel.writeSerializable(_file)
        parcel.writeParcelable(_image, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val imageItem = other as ImageItem
        return _file?.path == imageItem._file?.path
    }

    override fun hashCode(): Int {
        return Objects.hash(_file?.path)
    }

    companion object CREATOR : Parcelable.Creator<ImageItem> {
        override fun createFromParcel(parcel: Parcel): ImageItem {
            return ImageItem(parcel)
        }

        override fun newArray(size: Int): Array<ImageItem?> {
            return arrayOfNulls(size)
        }
    }
}
