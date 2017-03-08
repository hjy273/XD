package com.tyb.xd.view.animation;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * viewPager的切换动画
 * 通过改写可以实现多种多样的变化
 */
public class DepthPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.75f;
    @Override
    public void transformPage(View page, float position) {
        int pageWidth = page.getWidth();
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            page.setAlpha(0);

        } else if (position <= 0) { //A页效果 position 0 ~ -1
            // [-1,0]
            // Use the default slide transition when moving to the left page
            page.setAlpha(1-Math.abs(position));
          //  page.setTranslationX(pageWidth*position);
            page.setTranslationX(1);
            Float scale = 1-(1-MIN_SCALE)*(Math.abs(position));
            page.setScaleX(scale);
            page.setScaleY(scale);

        } else if (position <= 1) { //B页效果 position 1.0 ~ 0.0
            // (0,1]
            // Fade the page out.
            page.setAlpha(1 - position);
            // Counteract the default slide transition
            page.setTranslationX(pageWidth * -position);//position取了负号，而且position是从1~0，
            // 所以积会越来越大，即产生向右移动的效果

            // Scale the page down (between MIN_SCALE and 1)
            float scaleFactor = MIN_SCALE
                    + (1 - MIN_SCALE) * (1 - Math.abs(position));//0.75+0.25*[0,1]，也就是和逐渐增加到1
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            page.setAlpha(0);
        }
    }
}