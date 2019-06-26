package com.gary.imagelook;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity implements ViewPager.OnPageChangeListener {


    private static final int DURATION = 2500;
    private ViewPager imgListVp;
    private TextView imageNumTv;
    private RelativeLayout bgRl;
    private int imgPosition;
    private List<PhotoView> photoViewList;
    //开始的坐标值
    private int startY;
    private int startX;
    //开始的宽高
    private int startWidth;
    private int startHeight;
    //X、Y的移动距离
    private int xDelta;
    private int yDelta;
    //X、Y的缩放比例
    private float mWidthScale;
    private float mHeightScale;
    //背景色
    private ColorDrawable colorDrawable;
    // 当前选中的photoView
    private PhotoView curPhotoView;
    //所有图片的位置大小参数
    private ArrayList<ImgOptionEntity> optionEntities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        imgListVp = findViewById(R.id.viewpage);
        imageNumTv = findViewById(R.id.tv_count);
        bgRl = findViewById(R.id.bgRl);
        initView();
        initData();
    }

    protected void initView() {
        //修改状态栏颜色
//        StatusBarUtil.setColorNoTranslucent(this, Color.BLACK);
        //全屏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置背景色，后面需要为其设置渐变动画
        colorDrawable = new ColorDrawable(ContextCompat.getColor(this, android.R.color.black));
        bgRl.setBackground(colorDrawable);
    }

    protected void initData() {
        photoViewList = new ArrayList<>();
        imgPosition = getIntent().getIntExtra("Position", 0);
        //获取到当前所有ImageView对应的位置
        optionEntities = getIntent().getParcelableArrayListExtra("optionEntities");
        if (optionEntities != null && !optionEntities.isEmpty()) {
            //设置选中的位置来初始化动画
            ImgOptionEntity entity = optionEntities.get(imgPosition);
            startY = entity.getTop();
            startX = entity.getLeft();
            startWidth = entity.getWidth();
            startHeight = entity.getHeight();
            imageNumTv.setText(imgPosition + 1 + "/" + optionEntities.size());
            for (int i = 0; i < optionEntities.size(); i++) {
                addItemPhotoView(optionEntities.get(i).getImgUrl());
            }
            if (optionEntities.size() == 1) imageNumTv.setVisibility(View.GONE);
            else imageNumTv.setVisibility(View.VISIBLE);
        }
        ImageListAdapter imageListAdapter = new ImageListAdapter();
        imgListVp.setAdapter(imageListAdapter);
        imgListVp.setOnPageChangeListener(this);
        imgListVp.setCurrentItem(imgPosition);
        if (!photoViewList.isEmpty()) {
            curPhotoView = photoViewList.get(imgPosition);
            //注册一个回调函数，当一个视图树将要绘制时调用这个回调函数。
            ViewTreeObserver observer = curPhotoView.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    curPhotoView.getViewTreeObserver().removeOnPreDrawListener(this);
                    int[] screenLocation = new int[2];
                    curPhotoView.getLocationOnScreen(screenLocation);
                    //动画需要移动的距离
                    xDelta = startX - screenLocation[0];
                    yDelta = startY - screenLocation[1];
                    // 计算缩放比例
                    mWidthScale = (float) startWidth / curPhotoView.getWidth();
                    mHeightScale = (float) startHeight / curPhotoView.getHeight();
                    enterAnimation(new Runnable() {
                        @Override
                        public void run() { //开始动画之后要做的操作
                        }
                    }); //返回 true 继续绘制，返回false取消。
                    return true;
                }
            });
        }
    }

    private void enterAnimation(final Runnable enterAction) {
        //放大动画
        curPhotoView.setPivotX(0);
        curPhotoView.setPivotY(0);
        curPhotoView.setScaleX(mWidthScale);
        curPhotoView.setScaleY(mHeightScale);
        curPhotoView.setTranslationX(xDelta);
        curPhotoView.setTranslationY(yDelta);
        TimeInterpolator sDecelerator = new DecelerateInterpolator();
        curPhotoView.animate().setDuration(DURATION).scaleX(1).scaleY(1).translationX(0).translationY(0).setInterpolator(sDecelerator).withEndAction(enterAction);
        //设置背景渐变成你设置的颜色
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(colorDrawable, "alpha", 0, 255);
        bgAnim.setDuration(DURATION);
        bgAnim.start();
    }

    private void exitAnimation(final Runnable endAction) {
        //缩小动画
        curPhotoView.setPivotX(0);
        curPhotoView.setPivotY(0);
        curPhotoView.setScaleX(1);
        curPhotoView.setScaleY(1);
        curPhotoView.setTranslationX(0);
        curPhotoView.setTranslationY(0);
        TimeInterpolator sInterpolator = new AccelerateInterpolator();
        curPhotoView.animate().setDuration(DURATION).scaleX(mWidthScale).scaleY(mHeightScale).translationX(xDelta).translationY(yDelta).setInterpolator(sInterpolator).withEndAction(endAction);
        //设置背景渐透明
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(colorDrawable, "alpha", 0);
        bgAnim.setDuration(DURATION);
        bgAnim.start();
    }

    @Override
    public void onBackPressed() {
        int[] screenLocation = new int[2];
        curPhotoView.getLocationOnScreen(screenLocation);
        xDelta = startX - screenLocation[0];
        yDelta = startY - screenLocation[1];
        mWidthScale = (float) startWidth / curPhotoView.getWidth();
        mHeightScale = (float) startHeight / curPhotoView.getHeight();
        exitAnimation(new Runnable() {
            @Override
            public void run() { //结束动画要做的操作
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    private void addItemPhotoView(String imgUrlStr) {
        PhotoView photoView = new PhotoView(this);
        Glide.with(this).load(imgUrlStr).into(photoView);
        photoView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                onBackPressed();
            }
        });
        photoViewList.add(photoView);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        //当ViewPager滚动时重置成position对应外面的ImageView的位置信息
        curPhotoView = photoViewList.get(position);
        if (optionEntities != null && !optionEntities.isEmpty()) {
            ImgOptionEntity entity = optionEntities.get(position);
            startY = entity.getTop();
            startX = entity.getLeft();
            startWidth = entity.getWidth();
            startHeight = entity.getHeight();
            imageNumTv.setText(position + 1 + "/" + optionEntities.size());
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public class ImageListAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return photoViewList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        @NotNull
        public Object instantiateItem(android.view.ViewGroup container, int position) {
            container.addView(photoViewList.get(position));
            return photoViewList.get(position);
        }

        @Override
        public void destroyItem(android.view.ViewGroup container, int position, Object object) {
            container.removeView(photoViewList.get(position));
        }
    }
}
