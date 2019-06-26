package com.gary.imagelook

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Glide.with(this).load("https://ww1.sinaimg.cn/large/0065oQSqly1ftzsj15hgvj30sg15hkbw.jpg").into(image)
        image.setOnClickListener {
            val screenLocationS = IntArray(2)
            val optionEntities = ArrayList<ImgOptionEntity>()
            image.getLocationOnScreen(screenLocationS)
            var imgOptionEntity =
                ImgOptionEntity(
                    screenLocationS[0],
                    screenLocationS[1],
                    image.getWidth(),
                    image.getHeight(),
                    "https://ww1.sinaimg.cn/large/0065oQSqly1ftzsj15hgvj30sg15hkbw.jpg"
                )
            optionEntities.add(imgOptionEntity)
//            val intent = Intent(this@MainActivity, ImageActivity::class.java)
            val intent = Intent(this@MainActivity, Main2Activity::class.java)
            intent.putExtra("Position", 0)
            intent.putExtra("optionEntities", optionEntities)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }
}
