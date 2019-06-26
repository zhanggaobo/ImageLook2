package com.gary.imagelook

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.OnPhotoTapListener
import com.github.chrisbanes.photoview.PhotoView
import kotlinx.android.synthetic.main.activity_image.*
import kotlin.Boolean as Boolean1

class ImageActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {
    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
    }

    private val DURATION: Long = 250
    var position: Int = 0
    var optionEntities = ArrayList<ImgOptionEntity>()

    private lateinit var photoViewList: ArrayList<PhotoView>
    //开始的坐标值
    private var startY: Int = 0
    private var startX: Int = 0
    //开始的宽高
    private var startWidth: Int = 0
    private var startHeight: Int = 0
    //X、Y的移动距离
    private var xDelta: Int = 0
    private var yDelta: Int = 0
    //X、Y的缩放比例
    private var mWidthScale: Float = 0.toFloat()
    private var mHeightScale: Float = 0.toFloat()
    //背景色
    private var colorDrawable: ColorDrawable? = null
    //当前选中的photoView
    private var curPhotoView: PhotoView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        initView()
        initData()
    }

    private fun initView() {
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        //设置背景色，后面需要为其设置渐变动画
        colorDrawable = ColorDrawable(ContextCompat.getColor(this, android.R.color.black))
        bgRl.setBackground(colorDrawable)

    }

    private fun initData() {
        photoViewList = ArrayList()
        position = intent.getIntExtra("Position", 0)
        optionEntities = intent.getParcelableArrayListExtra("optionEntities")

        if (optionEntities != null && !optionEntities.isEmpty()) {
            //设置选中的位置来初始化动画
            var entity = optionEntities.get(position)
            startY = entity.top
            startX = entity.left
            startWidth = entity.width
            startHeight = entity.height
            var p = "" + (position + 1) + "/" + optionEntities.size
            tv_count.setText(p)
            for (optionEntitie in optionEntities) {
                addItemPhotoView(optionEntitie.imgUrl)
            }
            if (optionEntities.size == 1) tv_count.setVisibility(View.GONE)
            else tv_count.setVisibility(View.VISIBLE)
        }

        var imageListAdapter = ImageListAdapter(photoViewList)
        viewpage.setAdapter(imageListAdapter)
        viewpage.setOnPageChangeListener(this)
        viewpage.setCurrentItem(position)

        if (!photoViewList.isEmpty()) {
            curPhotoView = photoViewList[position]
            //注册一个回调函数，当一个视图树将要绘制时调用这个回调函数。
            val observer = curPhotoView!!.getViewTreeObserver()

            observer.addOnPreDrawListener {
                //                curPhotoView!!.getViewTreeObserver().removeOnPreDrawListener(this)
                var screenLocation = IntArray(2)
                curPhotoView!!.getLocationOnScreen(screenLocation)
                //动画需要移动的距离
                xDelta = startX - screenLocation[0]
                yDelta = startY - screenLocation[1]
                //计算缩放比例
                mWidthScale = (startWidth / curPhotoView!!.getWidth()).toFloat()
                mHeightScale = (startHeight / curPhotoView!!.getHeight()).toFloat()
                enterAnimation(Runnable { })
                true
            }
        }
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun enterAnimation(enterAction: Runnable) {
        curPhotoView!!.setPivotX(0F)
        curPhotoView!!.setPivotY(0F)
        curPhotoView!!.setScaleX(mWidthScale)
        curPhotoView!!.setScaleY(mHeightScale)
        curPhotoView!!.setTranslationX(xDelta.toFloat())
        curPhotoView!!.setTranslationY(yDelta.toFloat())
        val sDecelerator = DecelerateInterpolator()
        curPhotoView!!.animate().setDuration(DURATION).scaleX(1F).scaleY(1F)
            .translationX(0F).translationY(0F).setInterpolator(sDecelerator).withEndAction(enterAction)
        val bgAnim = ObjectAnimator.ofInt(colorDrawable, "alpha", 0, 255)
        bgAnim.setDuration(DURATION)
        bgAnim.start()
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun exitAnimation(endAction: Runnable) {
        curPhotoView!!.setPivotX(0F)
        curPhotoView!!.setPivotY(0F)
        curPhotoView!!.setScaleX(1F)
        curPhotoView!!.setScaleY(1F)
        curPhotoView!!.setTranslationX(0F)
        curPhotoView!!.setTranslationY(0F)
        val sInterpolator = AccelerateInterpolator()
        curPhotoView!!.animate().setDuration(DURATION).scaleX(mWidthScale).scaleY(mHeightScale)
            .translationX(xDelta.toFloat()).translationY(yDelta.toFloat())
            .setInterpolator(sInterpolator).withEndAction(endAction)
        val bgAnim = ObjectAnimator.ofInt(colorDrawable, "alpha", 0)
        bgAnim.duration = DURATION
        bgAnim.start()
    }

    override fun onBackPressed() {
        val screenLocation = IntArray(2)
        curPhotoView!!.getLocationOnScreen(screenLocation)
        xDelta = startX - screenLocation[0]
        yDelta = startY - screenLocation[1]
        mWidthScale = startWidth as Float / curPhotoView!!.getWidth()
        mHeightScale = startHeight as Float / curPhotoView!!.getHeight()
        exitAnimation(Runnable {
            finish()
            overridePendingTransition(0, 0)
        })
    }

    private fun addItemPhotoView(imgUrlStr: String?) {
        val photoView = PhotoView(this)
        Glide.with(this).load(imgUrlStr).into(photoView)
        photoView.setOnPhotoTapListener(OnPhotoTapListener { view, x, y ->
            onBackPressed()
        })
        photoViewList.add(photoView)
    }

}
