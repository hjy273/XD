package com.tyb.xd.utils.clip_image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by peiyuWang on 2016/6/23.
 */
public class clipZoomImageView extends ImageView implements
        ScaleGestureDetector.OnScaleGestureListener,
        View.OnTouchListener,
        ViewTreeObserver.OnGlobalLayoutListener {
    /**
     * 最大的缩放比例
     */
    private static  float MAX_SCALE = 4.0f;
    /**
     * 初始化时的缩放比例
     */
    private float initScale = 1.0f;
    /**
     * 存放Matrix矩阵的9个值
     * final类型表明这个matrixValues只能绑定第一次给它的对象，不能再指向其他的对象
     * 但是matrixValues数组里面的值是可以改变的
     */
    private final float[] matrixValues = new float[9];

    private boolean once = true;
    /**
     * 缩放手势
     */
    private ScaleGestureDetector mScaleGestureDetector = null;

    private Matrix mScaleMstrix = new Matrix();

    private float mScale = 0;

    private float mLastX = 0;

    private float mLastY = 0;

    private final float mTouchSlop = 1.0f;

    private int mLastPointCount = 0;

    private boolean isHorisantalDrag = false;

    private boolean isVerticalDrag = false;

    private boolean isCanDrag = false;


    private static  float SCALE_MID = 2.0f;

    //是否正在伸缩
    private boolean mIsScale = false;

    GestureDetector mGestureDetector;

    private int mHorizontalPadding=0;

    public clipZoomImageView(Context context) {
        this(context, null);
    }

    public clipZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public clipZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setScaleType(ScaleType.MATRIX);
        super.setOnTouchListener(this);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);

        /**
         * 双击事件
         */
        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {

                        if (mIsScale)
                            return true;

                        /**
                         * 双击点
                         */
                        float x = e.getX();
                        float y = e.getY();
                        if (getScale() < SCALE_MID) {
                            clipZoomImageView.this.post(new ScaleRunnable(SCALE_MID, x, y));
                            mIsScale = true;
                        }else{
                            clipZoomImageView.this.postDelayed(new ScaleRunnable(initScale,x,y),16);
                            mIsScale=true;
                        }
                        return true;
                    }
                });
    }


    /**
     * ScaleGestureDetector.OnScaleGestureListener方法
     *
     * @param detector
     * @return
     */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        /**
         * 这个是获取到手势开始前的伸缩比例
         */
        float scale = getScale();
        /**
         * 这个是手势开始后，当前的距离/之前的距离（getScale()方法获得的值）的比
         */
        float scaleFactor = detector.getScaleFactor();

        if (getDrawable() == null) {
            return true;
        }
        /**
         * 缩放的控制范围
         */
        if ((scale <= MAX_SCALE && scaleFactor > 1.0f)//如果放大适合屏幕后的倍数小于MAX_SCALE 而且现在的比之前的要大
                || (scale >= initScale && scaleFactor < 1.0f))//现在的要比之前的小而且还是大于原始的
        {
            /**
             * 通过scaleFactor*scale便可以得到当前的伸缩比例
             * 如果当前的伸缩比例比initScale的要小，那么当前的伸缩比值，scaleFactor的值
             * 要重置为initScale/scale;(scale值为伸缩手势开始前的值)
             *
             *
             */
            if (scaleFactor * scale < initScale)//最小就是只能放到原始图片的大小
            {
                scaleFactor = initScale / scale;
            }
            /**
             * 同上
             */
            if (scaleFactor * scale > MAX_SCALE)//最大只能放大到原始的MAX_SCALE倍
            {
                scaleFactor = MAX_SCALE / scale;
            }
            /**
             *设置伸缩比值
             */
            mScaleMstrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMstrix);
        }

        return true;
    }

    /**
     * ScaleGestureDetector.OnScaleGestureListener的方法
     *
     * @param detector
     * @return
     */
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    /**
     * ScaleGestureDetector.OnScaleGestureListener的方法
     *
     * @param detector
     */
    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    /**
     * View.OnTouchListener的方法
     * 把触碰事件传到ScaleGestureDetector
     *
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if( mGestureDetector.onTouchEvent(event))
            return true;//如果手势被双击处理了，则返回
        mScaleGestureDetector.onTouchEvent(event);
        /**
         * 下面是移动图片
         */
        //获取触摸点
        float x = 0, y = 0;

        //获取触摸点数
        int pointCount = event.getPointerCount();
        /**
         * 获取多个触点的平均x y坐标
         */
        for (int i = 0; i < pointCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x = x / pointCount;
        y = y / pointCount;
        if (pointCount != mLastPointCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
            mLastPointCount = pointCount;
        }

        /**
         * 处理onTouch事件
         */
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                /**
                 * 因为只能检测到第一个点按下的事件
                 * 所以在这里没有使用到
                 */
                break;

            case MotionEvent.ACTION_CANCEL:
                mLastPointCount = 0;
                break;
            case MotionEvent.ACTION_MOVE:

                float detlaX = x - mLastX;
                float detlaY = y - mLastY;
                if (!isCanDrag) {
                    isCanDrag = isCanDrag(detlaX, detlaY);
                }
                if (isCanDrag) {
                    isHorisantalDrag = isVerticalDrag = true;

                    Drawable d = getDrawable();
                    if (d != null) {
                        RectF f = getMatrixRectF();
                        /**
                         * 当图片的宽度小于圆圈的直径时不能移动
                         */
                        if (f.width() <= getWidth()-2*mHorizontalPadding) {
                            detlaX = 0;
                            isHorisantalDrag = false;
                        }
                        /**
                         * 如果屏幕的高度小于圆圈的直径时不能移动
                         */
                        if (f.height() < getWidth()-2*mHorizontalPadding) {
                            detlaY = 0;
                            isVerticalDrag = false;
                        }
                        /**
                         * Matrix的值改变之后才，才知道屏幕的边界是否是放大后图片的边界
                         */
                        mScaleMstrix.postTranslate(detlaX, detlaY);
                        checkBorderToDrag();
                        setImageMatrix(mScaleMstrix);
                    }
                }
                mLastX = x;
                mLastY = y;
                break;

            case MotionEvent.ACTION_UP:
                mLastPointCount = 0;
                break;
        }
        return true;
    }

    private void checkBorderToDrag() {

        RectF f = getMatrixRectF();

        float dx = 0, dy = 0;

        if (isHorisantalDrag && f.left > mHorizontalPadding) {
            dx = -f.left+mHorizontalPadding;
        }
        if (isHorisantalDrag && f.right < getWidth()-mHorizontalPadding) {
            dx = getWidth() -mHorizontalPadding- f.right;
        }
        if (isVerticalDrag && f.top > getHeight()/2-(getWidth()/2-mHorizontalPadding)) {
            dy = -f.top+getHeight()/2-(getWidth()/2-mHorizontalPadding);
        }
        if (isVerticalDrag && f.bottom < getHeight()/2+(getWidth()/2-mHorizontalPadding)) {
            dy = getHeight()/2+(getWidth()/2-mHorizontalPadding)- f.bottom;
        }
        mScaleMstrix.postTranslate(dx, dy);
    }

    private boolean isCanDrag(float detlaX, float detlaY) {

        /**
         * 判断是否为移动行为
         */
        return Math.sqrt(detlaX * detlaX + detlaY * detlaY) > mTouchSlop;
    }

    /**
     * ViewTreeObserver.OnGlobalLayoutListener的方法
     */
    @Override
    public void onGlobalLayout() {
        if (once) {
            Drawable drawable = getDrawable();
            if (drawable == null)
                return;
            int viewWidth = getWidth()-2*mHorizontalPadding;
            int viewHeight = viewWidth;
            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();
            float scale = 1.0f;

            if (viewHeight < drawableHeight && viewWidth < drawableWidth) {
                scale=Math.max(viewWidth*1.0f/drawableWidth,viewHeight*1.0f/drawableHeight);
            }
            if (viewHeight < drawableHeight && viewWidth >= drawableWidth) {
                scale = viewWidth*1.0f/drawableWidth;
            }
            if (viewHeight > drawableHeight && viewWidth < drawableWidth) {
                scale = viewHeight*1.0f/drawableHeight;
            }
            if(viewHeight>drawableHeight&&viewWidth>drawableWidth)
            {
                scale = Math.max(viewHeight*1.0f/drawableHeight,
                        viewWidth*1.0f/drawableWidth);
            }
            initScale = scale;
            MAX_SCALE = scale*4.0f;
            SCALE_MID = scale*2.0f;
            mScaleMstrix.postTranslate((getWidth() - drawableWidth) / 2, (getHeight() - drawableHeight) / 2);
            mScaleMstrix.postScale(scale, scale, getWidth() / 2, getHeight() / 2);
            setImageMatrix(mScaleMstrix);
            once = false;
        }

    }

    public float getScale() {
        mScaleMstrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {

        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    private void checkBorderAndCenterWhenScale() {

        //需要偏移的宽度
        float detlax = 0;
        //需要偏移的高度
        float detlay = 0;
        //圆圈直径的宽度
        int width = getWidth()-2*mHorizontalPadding;
        RectF rectF = getMatrixRectF();
        if (rectF.height()<width)
        {
            float scaleY = width*1.0f/rectF.height();
            mScaleMstrix.postScale(1.0f,scaleY,(int)getWidth()/2,(int)getHeight()/2);
        }
        if (rectF.width()<width)
        {
            float scaleX = width*1.0f/rectF.width();
            mScaleMstrix.postScale(scaleX,1.0f,(int)getWidth()/2,(int)getHeight()/2);
        }
        /**
         * 如果图片的宽度比圆圈大
         * 如果左边有空白边，应该向左移动
         * 右边同理
         */
        rectF = getMatrixRectF();
        if (rectF.width() >= width-3) {
            if (rectF.left > mHorizontalPadding)//表示左边有白边
            {
                detlax = -rectF.left+mHorizontalPadding;
            }
            if (rectF.right < width+mHorizontalPadding)//如果右边有白边则进行右移
            {
                detlax = width+mHorizontalPadding - rectF.right;
            }
        }
        /**
         * 如果图片的高度大于圆圈的高度
         * 则如果上边有白，则上移
         */
        if (rectF.height() >= width-3) {
            if (rectF.top > getHeight()/2-(getWidth()/2-mHorizontalPadding))  //上边大于0，表示上边有空白
            {
                detlay = -rectF.top+getHeight()/2-(getWidth()/2-mHorizontalPadding);
            }
            if (rectF.bottom < getHeight()/2+(getWidth()/2-mHorizontalPadding))//下边小于高度则表示下边有空白
            {
                detlay = getHeight()/2+(getWidth()/2-mHorizontalPadding) - rectF.bottom;
            }
        }
        mScaleMstrix.postTranslate(detlax, detlay);
    }


    private RectF getMatrixRectF() {
        Drawable d = getDrawable();
        if(d==null)
            return new RectF(0,0,0,0);//默认值
        RectF f = new RectF(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        mScaleMstrix.mapRect(f);
        return f;
    }

    private class ScaleRunnable implements Runnable {

        /**
         * 每次放大的倍数
         */
        private float SCALE_bIGGER = 1.1f;

        /**
         * 每次缩小的倍数
         */
        private float SCALE_SMALLER = 0.9f;

        /**
         * 伸缩点
         */
        private float x;
        private float y;

        /**
         * 需要伸缩的目标
         */
        private float mTargetScale = 0;

        private float scale = 0f;

        public ScaleRunnable(float mTargetScale, float x, float y) {
            this.mTargetScale = mTargetScale;
            this.x = x;
            this.y = y;
            /**
             * 根据传入的目标伸缩值，当前的伸缩值与目标伸缩值比较进行判断是放大还是缩小
             */
            if (getScale() < mTargetScale) {
                scale = SCALE_bIGGER;
            } else {
                scale = SCALE_SMALLER;
            }
        }

        @Override
        public void run() {

            mScaleMstrix.postScale(scale, scale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMstrix);
            /**
             * 如果缩放还没有达到要求，则再次把该Runnable执行
             */
            if ((getScale() < mTargetScale && scale == SCALE_bIGGER)
                    || (getScale() > mTargetScale && scale == SCALE_SMALLER)) {
                /**
                 *再次加入线程，让其执行，直到达到目标值
                 */
                clipZoomImageView.this.postDelayed(this, 16);
            } else {//因为每次放大和缩小的倍数是规定好的
                //可能最后放大的倍数会比目标放大倍数大，此时就要缩放回来。也可能最后缩放的倍数比目标大，此时就应该放大回来
                float finalScale = getScale() / mTargetScale;//获取最后的伸缩比例
                mScaleMstrix.postScale(finalScale, finalScale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMstrix);
                mIsScale = false;
            }
        }
    }

    public Bitmap clip()
    {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        /**
         * 将屏幕上的内容绘制到画布上
         */
        draw(canvas);

        return getCircleBitmap(Bitmap.createBitmap(bitmap,mHorizontalPadding,
                getHeight()/2-(getWidth()/2-mHorizontalPadding),getWidth()-2*mHorizontalPadding,getWidth()-2*mHorizontalPadding));


    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {

        Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(circleBitmap);
        final int color = 0xff424242;
        final RectF rectF = new RectF(0,0,bitmap.getWidth(),bitmap.getHeight());
        Paint p = new Paint();
        p.setAntiAlias(true);
        canvas.drawARGB(0,0,0,0);
        p.setColor(color);
        canvas.drawCircle(bitmap.getWidth()/2,bitmap.getWidth()/2
        ,bitmap.getWidth()/2,p);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap,null,rectF,p);
        return circleBitmap;
    }

    public void setmHorizontalPadding(int horizontalPadding)
    {
        mHorizontalPadding = horizontalPadding;
    }
}
