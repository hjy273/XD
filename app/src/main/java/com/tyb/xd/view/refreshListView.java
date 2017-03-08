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

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 *
 */
public class refreshListView extends ListView implements AbsListView.OnScrollListener {
    View mViewHeader;
    View mViewFooter;
    TextView txttip;
    TextView txtupdatetime;
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
    private int footHeight;   //底部的高度
    boolean isScrollToButtom;//判断是否滚动到底部
    boolean isRemarkOnFooter;//判断是否是在底部按下的
    boolean isrefresh;
    boolean isLoadingMore;//判断是否是加载更多的状态
    private int mtotal = 0;

    public refreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    public refreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public refreshListView(Context context) {
        super(context);
        initView(context);
    }

    /**
     * 初始化顶部布局
     */
    private void initView(Context context) {
        //初始化头尾布局
        initHeaderView(context);
        initFooterView(context);
        isrefresh = false;
    }

    private void initFooterView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mViewFooter = inflater.inflate(R.layout.footview, null);
        ProgressBar progressBarfooter = (ProgressBar) mViewFooter.findViewById(R.id.progressbar_refresh_footview);
        /**
         * 下面两句是调用View下的 measure(int widthMeasureSpec, int heightMeasureSpec) 方法，
         * 将widthMeasureSpec和heightMeasureSpec分别设置为0，
         * 这里的widthMeasureSpec和heightMeasureSpec并不是一个准备的值，
         * 而且指定一个规格或者标准让系统帮我们测量View的宽高，
         * 当我们指定widthMeasureSpec和heightMeasureSpec分别为0的时候，
         * 系统将不采用这个规格去测量，而是根据实际情况去测量。
         * 之后，我们可以调用View下的 getMeasuredHeight() 方法获取真实的头布局的高度，
         * 然后设置top = - 头布局实际高度，实现隐藏头布局。
         */
        mViewFooter.measure(0, 0);
        footHeight = mViewFooter.getMeasuredHeight();
        hideFooter(-footHeight);
        this.addFooterView(mViewFooter);
    }

    private void initHeaderView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mViewHeader = inflater.inflate(R.layout.headerview, null);
        txttip = (TextView) mViewHeader.findViewById(R.id.txt_tip);
        txtupdatetime = (TextView) mViewHeader.findViewById(R.id.txt_lastuptime);
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

    private void hideFooter(int downPadding) {
        mViewFooter.setPadding(mViewFooter.getPaddingLeft(), downPadding,
                mViewFooter.getPaddingRight(), mViewFooter.getPaddingBottom());
        mViewFooter.invalidate();
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
        mtotal = totalItemCount;
        if ((totalItemCount > 2) && (getLastVisiblePosition() == (totalItemCount - 1))) {
            isScrollToButtom = true;
        } else
            isScrollToButtom = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isrefresh) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    if (firstVisibleItem == 0) {
                        isRemark = true;
                        startY = (int) ev.getY();
                    }
                    if (isScrollToButtom) {
                        startY = (int) ev.getY();
                        isLoadingMore = false;
                        isRemarkOnFooter = true;
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
                        IRefreshListener.onRefreshOnHeader();
                    } else if (state == PULL) {
                        state = NONE;
                        isRemark = false;
                        refreshView();
                    }
                    if (isLoadingMore) {
                        hideFooter(0);
                        isrefresh = true;
                        IRefreshListener.onRefreshOnFooter();
                    } else
                        hideFooter(-footHeight);
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
                    if (mtotal < 4) {
                        if (distance > headHeight + 15) {
                            state = REFRESH;
                            refreshView();
                        }
                    } else {
                        if ((distance > headHeight + 40) && scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                            state = REFRESH;
                            refreshView();
                        }
                    }
                    break;
                case REFRESH:
                    if (distance < headHeight + 50)
                        hideHearer(topPadding);
                    else
                        hideHearer(50);
                    if (mtotal < 4) {
                        if (distance < headHeight+5) {
                            state = PULL;
                            refreshView();
                        }
                    } else {
                        if (distance < headHeight + 30) {
                            state = PULL;
                            refreshView();
                        } else if (distance <= 0) {
                            state = NONE;
                            isRemark = false;
                            refreshView();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        if (isRemarkOnFooter) {
            int currentY = (int) ev.getY();
            int distance = startY - currentY;
            if (distance > footHeight + 10) {
                startY -= distance - footHeight - 10;
                isLoadingMore = true;
                hideFooter(10);
            } else {
                int topPadding = distance - footHeight;
                hideFooter(topPadding);
            }
            if (distance <= footHeight - 10)
                isLoadingMore = false;
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
                txttip.setText(getResources().getString(R.string.drowdowncanrefresh));
                imgArrow.clearAnimation();
                imgArrow.setAnimation(arrow2);
                break;
            case REFRESH:
                imgArrow.setVisibility(VISIBLE);
                progressbar.setVisibility(GONE);
                txttip.setText(getResources().getString(R.string.drowupcanget));
                imgArrow.clearAnimation();
                imgArrow.setAnimation(arrow1);
                break;
            case REFRESHING:
                hideHearer(0);
                imgArrow.setVisibility(GONE);
                progressbar.setVisibility(VISIBLE);
                txttip.setText(getResources().getString(R.string.refreshing));
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
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String time = format.format(date);
        txtupdatetime.setText(getResources().getString(R.string.preupdatetime) + time);

    }

    public void refreshCompleteOnFooter() {
        hideFooter(-footHeight);
        isLoadingMore = false;
        isLoadingMore = false;
        isrefresh = false;
    }

    public void setInterface(IRefreshListener IRefreshListener) {
        this.IRefreshListener = IRefreshListener;
    }

    /**
     * 刷新数据的接口
     */
    public interface IRefreshListener {
        public void onRefreshOnHeader();

        public void onRefreshOnFooter();
    }
}
