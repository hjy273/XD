package com.tyb.xd.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tyb.xd.R;
import com.tyb.xd.utils.SharePreferenceUtils;
import com.tyb.xd.view.CircleIndicator;
import com.tyb.xd.view.animation.DepthPageTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 表示首次进入
 */
public class GuideActivity extends Activity {


    private List<View> mView = new ArrayList<View>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setContentView(R.layout.ac_guide);
        ViewPager viewpager = (ViewPager) findViewById(R.id.id_ac_guide_vp);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.id_ac_guide_indicator);
        initViewPager();
        viewpager.setAdapter(new welcomePagerAdaper(mView));
        viewpager.setPageTransformer(true, new DepthPageTransformer());
       // indicator.setViewPager(viewpager);
    }

    /**
     * 初始化viewpager中的view
     */
    private void initViewPager() {
        int[] imgId = new int[]{R.drawable.leading1, R.drawable.leading2, R.drawable.leading3};
        for (int i = 0; i < 3; i++) {
            ImageView imgLeading = new ImageView(this);
            imgLeading.setImageResource(imgId[i]);
            imgLeading.setScaleType(ImageView.ScaleType.FIT_XY);
            if(i==2)
            {
                imgLeading.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GuideActivity.this, HomeActivity.class);
                        SharePreferenceUtils.setNotFirstInApp(GuideActivity.this);
                        startActivity(intent);
                        finish();
                    }
                });
            }
            mView.add(imgLeading);
        }
    }


    /**
     * 首次进入时viewPager的adapter
     */
    public class welcomePagerAdaper extends PagerAdapter {

        private List<View> mView;

        public welcomePagerAdaper(List<View> mView) {
            this.mView = mView;
        }

        @Override
        public int getCount() {
            return mView.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup view, int position, Object object) {
            view.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            view.addView(mView.get(position), ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            return mView.get(position);
        }
    }
}
