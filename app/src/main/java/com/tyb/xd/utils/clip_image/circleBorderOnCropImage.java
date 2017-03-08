package com.tyb.xd.utils.clip_image;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
/**
 * Created by peiyuWang on 2016/6/23.
 */

/**
 * 裁剪图片时显示的圆圈，表示要裁剪的部分
 */
public class circleBorderOnCropImage extends View  {

    private int mBorderWidth = 2;

    private int mHorizontalPadding=60;

    private Paint mPaint;

    public circleBorderOnCropImage(Context context) {
        this(context,null);
    }

    public circleBorderOnCropImage(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public circleBorderOnCropImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBorderWidth = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,mBorderWidth,
                getResources().getDisplayMetrics());
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mBorderWidth);
        mPaint.setColor(Color.parseColor("#FFFFFF"));
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize,heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(getWidth()/2,getHeight()/2,getWidth()/2-mHorizontalPadding,mPaint);
    }

    public void setmBorderWidth(int mBorderWidth) {
        this.mBorderWidth = mBorderWidth;
    }

    public void setmHorizontalPadding(int mHorizontalPadding) {

        this.mHorizontalPadding = mHorizontalPadding;
    }
}
