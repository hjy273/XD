package com.tyb.xd.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


/**
 * viewpager的adapter
 * @date: 2015年7月3日 下午4:40:54
 * QQ2050542273
 * @email:15162925211@163.com
 */
public class FacePageAdeapter extends PagerAdapter implements ViewPager.OnPageChangeListener{

    // 界面列表
    private List<View> views;
    private ViewPager viewPager;

    public FacePageAdeapter(List<View> lv, ViewPager viewPager) {
        super();
        this.views = lv;
        this.viewPager = viewPager;
        // TODO Auto-generated constructor stub
        viewPager.setAdapter(this);
        viewPager.setOnPageChangeListener(this);
    }

    @Override
    public int getCount() {
        if (views != null) {
            return views.size();
        }
        return 0;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }




    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position));
        return views.get(position);
    }


    @Override
    public void startUpdate(ViewGroup container) {
        super.startUpdate(container);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return (arg0 == arg1);
    }

    /**
     * pager change listener
     * @param position
     * @param positionOffset
     * @param positionOffsetPixels
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
