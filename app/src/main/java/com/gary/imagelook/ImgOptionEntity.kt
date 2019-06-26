package com.gary.imagelook

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by zhanggaobo
 * Date :2019/6/25/025
 * Description :
 * Version :1.0
 */
class ImgOptionEntity() : Parcelable {
    var left: Int = 0
    var top: Int = 0
    var width: Int = 0
    var height: Int = 0
    var imgUrl: String? = null

    constructor(left: Int, top: Int, width: Int, height: Int, imgUrl: String?) : this() {
        this.left = left
        this.top = top
        this.width = width
        this.height = height
        this.imgUrl = imgUrl
    }

    constructor(parcel: Parcel) : this() {
        left = parcel.readInt()
        top = parcel.readInt()
        width = parcel.readInt()
        height = parcel.readInt()
        imgUrl = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(left)
        parcel.writeInt(top)
        parcel.writeInt(width)
        parcel.writeInt(height)
        parcel.writeString(imgUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImgOptionEntity> {
        override fun createFromParcel(parcel: Parcel): ImgOptionEntity {
            return ImgOptionEntity(parcel)
        }

        override fun newArray(size: Int): Array<ImgOptionEntity?> {
            return arrayOfNulls(size)
        }
    }

}