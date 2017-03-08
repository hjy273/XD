package com.tyb.xd.view;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tyb.xd.R;
import com.tyb.xd.adapter.ExpandAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 作者：long2ice on 2016/08/01 16:07
 * 邮箱：343178315@qq.com
 */
public class MyExpandListView extends ExpandableListView implements AbsListView.OnScrollListener {
    public void setAdapter(ExpandAdapter adapter) {
        super.setAdapter(adapter);
    }

    //松开刷新
    private final static int RELEASE_TO_REFRESH = 0;//
    private final static int PULL_TO_REFRESH = 1;//下拉刷新

    private final static int REFRESHING = 2;//正在刷新

    private final static int DONE = 3;
    private final static int RATIO = 3;//实际的padding的距离与界面 上偏移距离 的比例
    private LayoutInflater inflater;
    private LinearLayout headLayout;//头linearlayout
    private TextView tipsTextview;
    private ImageView arrowImageView;//箭头的图标
    private int scrollState;//当前的滚动状态
    private ProgressBar progressBar;
    private TextView mtvLastuptime;
    private RotateAnimation animation;
    // 反转动画
    private RotateAnimation reverseAnimation;

    private LinearLayout headView;
    private int headContentHeight;
    private String mlastUpTime = "";
    /**
     * 手势按下的起点位置
     */
    private int startY;
    private int firstItemIndex;
    private int state;
    private boolean isBack;

    private IRefreshListener IRefreshListener;//刷新数据的接口

    private boolean isRefreshable;

    public MyExpandListView(Context context) {
        super(context);
        init(context);
    }

    public MyExpandListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void onClick(DialogInterface dialog, int which) {

    }

    public void init(Context context) {
        inflater = LayoutInflater.from(context);
        headView = (LinearLayout) inflater.inflate(R.layout.headerview, null);
        arrowImageView = (ImageView) headView.findViewById(R.id.img_arrow);//箭头
        mtvLastuptime = (TextView) headView.findViewById(R.id.txt_lastuptime);//上次刷新时间
        arrowImageView.setMinimumWidth(70);
        arrowImageView.setMinimumHeight(50);
        progressBar = (ProgressBar) headView.findViewById(R.id.progressbar_refresh);
        tipsTextview = (TextView) headView.findViewById(R.id.txt_tip);

        headView.measure(0, 0);
        headContentHeight = headView.getMeasuredHeight();
        hideHeader(headContentHeight);
        this.addHeaderView(headView);
        this.setOnScrollListener(this);//滚动监听

        animation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(250);
        animation.setFillAfter(true);

        reverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(200);
        reverseAnimation.setFillAfter(true);

        state = DONE;
        isRefreshable = false;
    }

    private void hideHeader(int topPadding) {
        headView.setPadding(headView.getPaddingLeft(), -topPadding, headView.getPaddingRight(), headView.getPaddingBottom());
        headView.invalidate();
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        firstVisibleItem = firstVisibleItem;

    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState = scrollState;
    }

    /**
     * 设置触摸事件 总的思路就是
     * 1 ACTION_DOWN：记录起始位置
     * 2 ACTION_MOVE：计算当前位置与起始位置的距离，来设置state的状态
     * 3 ACTION_UP：根据state的状态来判断是否下载
     */
    public boolean onTouchEvent(MotionEvent event) {
        isRefreshable = true;
        if (isRefreshable) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN://按下屏幕
                    System.out.println("按下屏");
                    if (firstItemIndex == 0) {
                        startY = (int) event.getY();
                        System.out.println("记录down下当前的位置");
                    }
                    break;
                case MotionEvent.ACTION_MOVE: //移动屏幕
                    System.out.println("移动下屏");
                    int tempY = (int) event.getY();
                    if (state == PULL_TO_REFRESH) {
                        setSelection(0);//很重要
                        //下拉到可以release_to_refresh的状态
                        if ((tempY - startY) / RATIO >= headContentHeight) {
                            state = RELEASE_TO_REFRESH;
                            isBack = true;
                            changeHeaderViewByState();
                        }
                        //上推到顶了
                        else if (tempY - startY <= 0) {
                            state = DONE;
                            changeHeaderViewByState();
                        }
                        headView.setPadding(0, -headContentHeight + (tempY - startY) / RATIO, 0, 0);
                    }
                    if (state == RELEASE_TO_REFRESH) {
                        setSelection(0);
                        // 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
                        if (((tempY - startY) / RATIO < headContentHeight) && (tempY - startY) > 0) {
                            state = PULL_TO_REFRESH;
                            changeHeaderViewByState();
                        }
                        headView.setPadding(0, -headContentHeight + (tempY - startY) / RATIO, 0, 0);
                    }
                    // done状态下
                    if (state == DONE) {
                        if (tempY - startY > 0) {
                            state = PULL_TO_REFRESH;
                            changeHeaderViewByState();
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    System.out.println("ACTION_UP");
                    if (state != REFRESHING) {
                        // 不在刷新状态
                        if (state == PULL_TO_REFRESH) {
                            state = DONE;
                            changeHeaderViewByState();
                        }
                        if (state == RELEASE_TO_REFRESH) {
                            state = REFRESHING;
                            changeHeaderViewByState();
                            isRefreshable = true;
                            IRefreshListener.onRefresh();
                        }
                    }
                    isBack = false;
                    break;

            }
        }
        return super.onTouchEvent(event);

    }

    //当状态改变时候，调用 该方法，以更新界面
    private void changeHeaderViewByState() {
        switch (state) {
            case RELEASE_TO_REFRESH:
                arrowImageView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                tipsTextview.setVisibility(View.VISIBLE);

                arrowImageView.clearAnimation();
                arrowImageView.startAnimation(animation);
                tipsTextview.setText("松开刷新");

                break;
            case PULL_TO_REFRESH:
                progressBar.setVisibility(View.GONE);
                tipsTextview.setVisibility(View.VISIBLE);

                arrowImageView.clearAnimation();
                arrowImageView.setVisibility(View.VISIBLE);
                tipsTextview.setText("下拉刷新");
                // 是RELEASE_To_REFRESH状态转变来的
                if (isBack) {
                    isBack = false;
                    arrowImageView.startAnimation(reverseAnimation);
                }
                break;

            case REFRESHING:
                headView.setPadding(0, 0, 0, 0);
                progressBar.setVisibility(View.VISIBLE);
                arrowImageView.clearAnimation();
                arrowImageView.setVisibility(View.GONE);
                tipsTextview.setText("正在刷新...");
                break;
            case DONE:
                headView.setPadding(0, -headContentHeight, 0, 0);
                progressBar.setVisibility(View.GONE);
                arrowImageView.clearAnimation();
                arrowImageView.setImageResource(R.drawable.arrow);
                tipsTextview.setText("下拉刷新");
                break;
        }
    }

    public void refreshCompleteOnHeader() {
        state = DONE;
        isRefreshable = false;
        isBack = false;
        changeHeaderViewByState();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=new Date();
        mlastUpTime = "" + format.format(date);
        String text = "上次刷新时间：" + mlastUpTime;
        mtvLastuptime.setText(text);
    }

    public interface IRefreshListener {
        public void onRefresh();
    }

    public void setInterface(IRefreshListener IRefreshListener) {
        this.IRefreshListener = IRefreshListener;
    }
}
