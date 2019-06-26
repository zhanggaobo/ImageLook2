package com.gary.imagelook

import android.view.View
import androidx.viewpager.widget.PagerAdapter
import com.github.chrisbanes.photoview.PhotoView

/**
 * Created by zhanggaobo
 * Date :2019/6/25/025
 * Description :
 * Version :1.0
 */
class ImageListAdapter : PagerAdapter {

    private var photoViewList: ArrayList<PhotoView>

    constructor(photoViewList: ArrayList<PhotoView>) {
        this.photoViewList = photoViewList
    }

    override fun getCount(): Int {
        return photoViewList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    /**
     * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
     */
    override fun instantiateItem(container: android.view.ViewGroup, position: Int): Any {
        container.addView(photoViewList[position])
        return photoViewList[position]
    }

    override fun destroyItem(container: android.view.ViewGroup, position: Int, `object`: Any) {
        container.removeView(photoViewList[position])
    }
}