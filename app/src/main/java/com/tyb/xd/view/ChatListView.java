package com.tyb.xd.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tyb.xd.R;

/**
 * Created by peiyu Wang on 2016/3/11.
 */
public class ChatListView extends ListView implements AbsListView.OnScrollListener {
    View mViewHeader;
    View mViewFooter;
    TextView txttip;
    ImageView imgArrow;
    ProgressBar progressbar;
    int headHeight;//顶部布局的高度
    int firstVisibleItem;//当前listview第一个可见的位置
    boolean isRemark; //标记当前实在listview的顶端按下的
    int startY;//摁下时的Y值
    int state = 0;//当前的状态
    int scrollState;//当前的滚动状态
    final int NONE = 0; //正常状态
    final int PULL = 1; //提示下拉刷新状态
    final int REFRESH = 2; //提示释放立即刷新状态
    final int REFRESHING = 3; //提示正在刷新状态
    IRefreshListener IRefreshListener;//刷新数据的接口
    private ProgressBar progressBarfooter; //加载功能的进度条
    boolean isrefresh;
    boolean isLoadingMore;//判断是否是加载更多的状态

    public ChatListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    public ChatListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ChatListView(Context context) {
        super(context);
        initView(context);
    }

    /**
     * 初始化顶部布局
     */
    private void initView(Context context) {
        //初始化头尾布局
        initHeaderView(context);
        isrefresh = false;
    }



    private void initHeaderView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mViewHeader = inflater.inflate(R.layout.headerview, null);
        txttip = (TextView) mViewHeader.findViewById(R.id.txt_tip);
        imgArrow = (ImageView) mViewHeader.findViewById(R.id.img_arrow);
        progressbar = (ProgressBar) mViewHeader.findViewById(R.id.progressbar_refresh);
        measureView(mViewHeader);
        headHeight = mViewHeader.getMeasuredHeight();
        hideHearer(-headHeight);
        this.addHeaderView(mViewHeader);
        this.setOnScrollListener(this);
    }

    /**
     * 通知父布局占用多少位置，比较麻烦，可以使用footview的方法
     *
     * @param view
     */
    private void measureView(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null)
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int tempHeight = p.height;
        if (tempHeight > 0)
            headHeight = MeasureSpec.makeMeasureSpec(tempHeight, MeasureSpec.EXACTLY);
        else
            headHeight = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        view.measure(width, headHeight);
    }

    private void hideHearer(int topPadding) {
        mViewHeader.setPadding(mViewHeader.getPaddingLeft(), topPadding,
                mViewHeader.getPaddingRight(), mViewHeader.getPaddingBottom());
        mViewHeader.invalidate();
    }



    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState = scrollState;

    }

    /**
     * @param view
     * @param firstVisibleItem 第一个可见的位置
     * @param visibleItemCount
     * @param totalItemCount
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(!isrefresh) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    if (firstVisibleItem == 0) {
                        isRemark = true;
                        startY = (int) ev.getY();
                    }
                    break;
                }
                case MotionEvent.ACTION_MOVE:
                    onMove(ev);
                    break;
                case MotionEvent.ACTION_UP:
                    //加载最新数据
                    if (state == REFRESH) {
                        state = REFRESHING;
                        refreshView();
                        isrefresh = true;
                        if(IRefreshListener==null)
                        {
                            /**
                             * 刷新数据的借口为空，啥也不做
                             */
                        }else{
                            if(IRefreshListener.onRefreshOnHeader())
                            {
                                //表示刷新完成了
                                refreshCompleteOnHeader();

                            }
                        }
                    } else if (state == PULL) {
                        state = NONE;
                        isRemark = false;
                        refreshView();
                    }
                    break;
                default:
                    break;
            }
            return super.onTouchEvent(ev);
        }
        return super.onTouchEvent(ev);
    }

    private void onMove(MotionEvent ev) {
        if (isRemark) {
            int currentY = (int) ev.getY();
            int distance = currentY - startY;
            if (distance > headHeight + 50)
                startY += distance - headHeight - 50;
            int topPadding = distance - headHeight;
            switch (state) {
                case NONE:
                    if (distance > 0) {
                        state = PULL;
                        hideHearer(topPadding);
                        refreshView();
                    }
                    break;
                case PULL:
                    hideHearer(topPadding);
                    if ((distance > headHeight + 25) && scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                        state = REFRESH;
                        refreshView();
                    }
                    break;
                case REFRESH:
                    if (distance < headHeight + 50)
                        hideHearer(topPadding);
                    else
                        hideHearer(50);
                    if (distance < headHeight + 25) {
                        state = PULL;
                        refreshView();
                    } else if (distance <= 0) {
                        state = NONE;
                        isRemark = false;
                        refreshView();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void refreshView() {
        RotateAnimation arrow1 = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        arrow1.setDuration(500);
        arrow1.setFillAfter(true);
        RotateAnimation arrow2 = new RotateAnimation(180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        arrow2.setDuration(500);
        arrow2.setFillAfter(true);
        switch (state) {
            case NONE:
                hideHearer(-headHeight);
                break;
            case PULL:
                imgArrow.setVisibility(VISIBLE);
                progressbar.setVisibility(GONE);
                txttip.setText("下拉可以刷新");
                imgArrow.clearAnimation();
                imgArrow.setAnimation(arrow2);
                break;
            case REFRESH:
                imgArrow.setVisibility(VISIBLE);
                progressbar.setVisibility(GONE);
                txttip.setText("释放可以刷新");
                imgArrow.clearAnimation();
                imgArrow.setAnimation(arrow1);
                break;
            case REFRESHING:
                hideHearer(0);
                imgArrow.setVisibility(GONE);
                progressbar.setVisibility(VISIBLE);
                txttip.setText("正在刷新");
                imgArrow.clearAnimation();
                break;
            default:
                break;
        }
    }

    public void refreshCompleteOnHeader() {
        state = NONE;
        isRemark = false;
        isrefresh = false;
        refreshView();
    }


    public void setInterface(IRefreshListener IRefreshListener) {
        this.IRefreshListener = IRefreshListener;
    }

    /**
     * 刷新数据的接口
     */
    public interface IRefreshListener {
        public Boolean onRefreshOnHeader();
    }
}
