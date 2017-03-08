package com.tyb.xd.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by wangpeiyu on 2016/7/12.
 */
public class RewordStatuView extends View {

    private int mWidth;

    private int mHeight;

    private Paint mPaintComplete;
    private Paint mPaintNoComplete;

    /**
     * 悬赏的四个状态
     */
    public static int IPUBLISH = 0;
    public static int IGET = 1;
    public static int ICOMPLETE = 2;
    public static int IFINISH = 3;
    public static int mStatu = IPUBLISH;//默认状态为已发布
    public static String SPUBLISH = "已领取";
    public static String SGET = "已发送";
    public static String SCOMPLETE = "已送达";
    public static String SFINISH = "已收到";
    private int mtxtSize;
    private Rect mBound;
    private int mAngle = 0;
    private int mOffset = 0;


    private int mCompleteColor = Color.parseColor("#00dec9");
    private int mNoCompleteColor = Color.parseColor("#cdcdcd");


    public RewordStatuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RewordStatuView(Context context) {
        this(context, null);
    }

    public RewordStatuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        /**
         * 绿色和灰色画笔
         */
        mPaintComplete = new Paint();
        mPaintComplete.setColor(mCompleteColor);
        mPaintNoComplete = new Paint();
        mPaintNoComplete.setColor(mNoCompleteColor);
        /**
         * 设置文本的大小
         */
        mtxtSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
        /**
         * 获取文本的宽度
         */
        mBound = new Rect();
        mPaintComplete.setTextSize(mtxtSize);
        mPaintNoComplete.setTextSize(mtxtSize);
        mPaintComplete.getTextBounds(SPUBLISH, 0, SPUBLISH.length(), mBound);
        /**
         * 获取文本信息
         */

    }

    
    @Override
    protected void onDraw(Canvas canvas) {
        int paddingL = getPaddingLeft();
        int paddingR = getPaddingRight();
        int radiu = mHeight / 4;
        int divideWidth = (mWidth - paddingL - paddingR - 3 * 2 * radiu) / 2;
        int circleY = mHeight / 4;
        int line1L = paddingL + 2 * radiu;
        int line1R = line1L + divideWidth;
        int line2L = line1R + 2 * radiu;
        int line2R = line2L + divideWidth;
        switch (mStatu) {
            case 0:
                /**
                 * 画圆
                 */
                canvas.drawCircle(paddingL + radiu, mHeight / 4, radiu, mPaintNoComplete);
                canvas.drawCircle(paddingL + 2 * radiu + divideWidth + radiu, mHeight / 4, radiu, mPaintNoComplete);
                canvas.drawCircle(mWidth - paddingR - radiu, mHeight / 4, radiu, mPaintNoComplete);
                canvas.drawText(SGET, paddingL + radiu - mBound.width() / 2, mHeight / 2 + mBound.height(), mPaintNoComplete);
                canvas.drawText(SCOMPLETE, paddingL + 2 * radiu + divideWidth + radiu - mBound.width() / 2, mHeight / 2 + mBound.height(), mPaintNoComplete);
                canvas.drawText(SFINISH, mWidth - paddingR - radiu - mBound.width() / 2, mHeight / 2 + mBound.height(), mPaintNoComplete);
                /**
                 * 划线
                 */
                canvas.drawLine(line1L, circleY, line1R, circleY, mPaintNoComplete);
                canvas.drawLine(line2L, circleY, line2R, circleY, mPaintNoComplete);
                break;
            case 1:
                /**
                 * 画圆
                 */
                canvas.drawCircle(paddingL + 2 * radiu + divideWidth + radiu, mHeight / 4, radiu, mPaintNoComplete);
                canvas.drawCircle(mWidth - paddingR - radiu, mHeight / 4, radiu, mPaintNoComplete);
                canvas.drawText(SGET, paddingL + radiu - mBound.width() / 2, mHeight / 2 + mBound.height(), mPaintNoComplete);
                canvas.drawText(SCOMPLETE, paddingL + 2 * radiu + divideWidth + radiu - mBound.width() / 2, mHeight / 2 + mBound.height(), mPaintNoComplete);
                canvas.drawText(SFINISH, mWidth - paddingR - radiu - mBound.width() / 2, mHeight / 2 + mBound.height(), mPaintNoComplete);
                /**
                 * 划线
                 */
                canvas.drawLine(line1L, circleY, line1R, circleY, mPaintNoComplete);
                canvas.drawLine(line2L, circleY, line2R, circleY, mPaintNoComplete);
                if (mAngle <= 360) {
                    mPaintComplete.setStyle(Paint.Style.STROKE);
                    RectF rectF = new RectF(getPaddingLeft(), 0, getPaddingLeft() + 2 * radiu, 2 * radiu);
                    canvas.drawArc(rectF, 0, mAngle, false, mPaintComplete);
                    mAngle += 5;
                    mPaintComplete.setStrokeWidth(2);
                    mPaintComplete.setStyle(Paint.Style.FILL);
                    invalidate();
                } else {
                    canvas.drawCircle(paddingL + radiu, mHeight / 4, radiu, mPaintComplete);
                    canvas.drawText(SGET, paddingL + radiu - mBound.width() / 2, mHeight / 2 + mBound.height(), mPaintComplete);
                    mAngle = 0;
                }
                break;
            case 2:
                /**
                 * 画圆
                 */
                canvas.drawCircle(paddingL + radiu, mHeight / 4, radiu, mPaintComplete);
                canvas.drawCircle(paddingL + 2 * radiu + divideWidth + radiu, mHeight / 4, radiu, mPaintNoComplete);
                canvas.drawCircle(mWidth - paddingR - radiu, mHeight / 4, radiu, mPaintNoComplete);
                canvas.drawText(SGET, paddingL + radiu - mBound.width() / 2, mHeight / 2 + mBound.height(), mPaintComplete);
                canvas.drawText(SCOMPLETE, paddingL + 2 * radiu + divideWidth + radiu - mBound.width() / 2, mHeight / 2 + mBound.height(), mPaintNoComplete);
                canvas.drawText(SFINISH, mWidth - paddingR - radiu - mBound.width() / 2, mHeight / 2 + mBound.height(), mPaintNoComplete);
                canvas.drawLine(line1L, circleY, line1R, circleY, mPaintNoComplete);
                if (mOffset < divideWidth) {
                    /**
                     * 划线
                     */
                    canvas.drawLine(line1L, circleY, line1L + mOffset, circleY, mPaintComplete);
                    canvas.drawLine(line2L, circleY, line2R, circleY, mPaintNoComplete);
                    mOffset += 3;
                    invalidate();
                } else {
                    canvas.drawCircle(paddingL + 2 * radiu + divideWidth + radiu, mHeight / 4, radiu, mPaintComplete);
                    canvas.drawText(SCOMPLETE, paddingL + 2 * radiu + divideWidth + radiu - mBound.width() / 2, mHeight / 2 + mBound.height(), mPaintComplete);
                    canvas.drawLine(line1L, circleY, line1R, circleY, mPaintComplete);
                    canvas.drawLine(line2L, circleY, line2R, circleY, mPaintNoComplete);
                    mOffset = 0;
                }
                break;
            case 3:
                /**
                 * 画圆
                 */
                canvas.drawCircle(paddingL + radiu, mHeight / 4, radiu, mPaintComplete);
                canvas.drawCircle(paddingL + 2 * radiu + divideWidth + radiu, mHeight / 4, radiu, mPaintComplete);
                canvas.drawCircle(mWidth - paddingR - radiu, mHeight / 4, radiu, mPaintNoComplete);
                canvas.drawText(SGET, paddingL + radiu - mBound.width() / 2, mHeight / 2 + mBound.height(), mPaintComplete);
                canvas.drawText(SCOMPLETE, paddingL + 2 * radiu + divideWidth + radiu - mBound.width() / 2, mHeight / 2 + mBound.height(), mPaintComplete);
                canvas.drawText(SFINISH, mWidth - paddingR - radiu - mBound.width() / 2, mHeight / 2 + mBound.height(), mPaintNoComplete);
                canvas.drawLine(line2L, circleY, line2R, circleY, mPaintNoComplete);
                if (mOffset < divideWidth) {
                    /**
                     * 划线
                     */
                    canvas.drawLine(line1L, circleY, line1R, circleY, mPaintComplete);
                    canvas.drawLine(line2L, circleY, line2L + mOffset, circleY, mPaintComplete);
                    mOffset += 3;
                    invalidate();
                } else {
                    canvas.drawCircle(mWidth - paddingR - radiu, mHeight / 4, radiu, mPaintComplete);
                    canvas.drawText(SFINISH, mWidth - paddingR - radiu - mBound.width() / 2, mHeight / 2 + mBound.height(), mPaintComplete);
                    canvas.drawLine(line1L, circleY, line1R, circleY, mPaintComplete);
                    canvas.drawLine(line2L, circleY, line2R, circleY, mPaintComplete);
                    mOffset = 0;
                }
                break;
            default:
                break;
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
            mWidth = MeasureSpec.getSize(widthMeasureSpec);
        }
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            mHeight = MeasureSpec.getSize(heightMeasureSpec);
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    public void setmStatu(int mStatu) {
        this.mStatu = mStatu;
        invalidate();
    }
    public int getmStatu() {
        return mStatu;
    }
}
