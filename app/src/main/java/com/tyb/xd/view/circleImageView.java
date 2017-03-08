package com.tyb.xd.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by peiyuWang on 2016/6/23.
 */
public class circleImageView extends ImageView {

    private Paint mPaint;

    private Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);

    private Bitmap mMaskBitmap;

    private WeakReference<Bitmap> mWeakBitmap = new WeakReference<Bitmap>(null);

    private Drawable mImg;

    /**
     * @param context
     */
    public circleImageView(Context context) {
        this(context, null);
    }

    public circleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public circleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mImg = getDrawable();
    }

    @Override
    protected void onDraw(Canvas canvas) {
       // super.onDraw(canvas);

        Bitmap bitmap = mMaskBitmap == null ? null : mWeakBitmap.get();
        if (bitmap == null) {
            //拿到drawbale对象

            if (mImg != null) {
                int width = mImg.getIntrinsicWidth();
                int height = mImg.getIntrinsicHeight();

                /**
                 * 根据大小创建一个bitmap，让内容绘制在该bitmap上
                 */
                bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                float scale = 1.0f;//缩放比例
                scale = Math.max(getWidth() * 1.0f / width, getHeight() * 1.0f / height);
                mImg.setBounds(0, 0, (int) (width * scale), (int) (height * scale));
                /**
                 * 根据bitmap创建一个画布，让内容画在bitmap上
                 */
                Canvas drawBitmap = new Canvas(bitmap);
                mImg.draw(drawBitmap);
                if (mMaskBitmap == null || mMaskBitmap.isRecycled()) {
                    mMaskBitmap = getBitmap();
                }
                mPaint.reset();
                mPaint.setFilterBitmap(false);
                mPaint.setXfermode(mXfermode);//画笔设置xfermode模式
                //绘制形状
                drawBitmap.drawBitmap(mMaskBitmap, 0, 0, mPaint);//取两个图层重叠部分的下层
                mPaint.setXfermode(null);
                //将准备好的bitmap绘制出来
                canvas.drawBitmap(bitmap, 0, 0, null);
                //将bitmap缓存起来，避免每次调用onDraw，消耗内存
                mWeakBitmap = new WeakReference<Bitmap>(bitmap);
            }

        }
        if (bitmap != null) {
            mPaint.setXfermode(null);
            canvas.drawBitmap(bitmap, 0, 0, mPaint);
        }

        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(false);
        mPaint.setColor(Color.parseColor("#d5d1c8"));
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(getWidth()/2,getWidth()/2,getWidth()/2-5,mPaint);
        mPaint.reset();
    }

    private Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.BLACK);
        canvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2-5, p);
        return bitmap;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * 圆形的图片，要宽高一致
         */
        int desire = Math.min(getMeasuredHeight(), getMeasuredWidth());
        setMeasuredDimension(desire, desire);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        mWeakBitmap = null;
        if(mMaskBitmap!=null)
        {
            mMaskBitmap.recycle();
            mMaskBitmap = null;
        }
    }

    public void setBitmap(Bitmap bitmap)
    {
        mImg = new BitmapDrawable(bitmap);
        invalidate();
    }
    public void setDrawable(Drawable drawable )
    {
        mImg = drawable;
        invalidate();
    }

    public Drawable getImage() {
        return mImg;
    }
}
