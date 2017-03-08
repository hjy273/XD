package com.tyb.xd.utils.clip_image;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by peiyuWang on 2016/6/27.
 */
public class ClipImageLayout extends RelativeLayout {

    private int mHorizontalPadding = 60;

    private circleBorderOnCropImage border;

    private clipZoomImageView clipZoomImageView;

    public ClipImageLayout(Context context) {
        this(context, null);
    }


    public ClipImageLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipImageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        border = new circleBorderOnCropImage(context);
        clipZoomImageView = new clipZoomImageView(context);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(clipZoomImageView, lp);
        this.addView(border, lp);
        /**
         * 计算水平间距
         */
        mHorizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mHorizontalPadding, getResources().getDisplayMetrics());
        border.setmHorizontalPadding(mHorizontalPadding);
        clipZoomImageView.setmHorizontalPadding(mHorizontalPadding);


    }

    public void setmHorizontalPadding(int mHorizontalPadding) {
        this.mHorizontalPadding = mHorizontalPadding;
    }

    /**
     * 返回裁剪的图片
     * @return
     */
    public Bitmap clip()
    {
        return clipZoomImageView.clip();
    }

    public void setBitmap(Bitmap bitmap)
    {
        clipZoomImageView.setImageBitmap(bitmap);
    }


}
