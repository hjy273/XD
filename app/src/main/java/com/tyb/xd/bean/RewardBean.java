package com.tyb.xd.bean;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.tyb.xd.R;
import com.tyb.xd.fastbean.RewardDeliveries;

import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.x;

import java.io.Serializable;

/**
 * 悬赏列表
 */
public class RewardBean implements Serializable {

    /**
     * 物品的重量级
     */
    public static final int IWEIGHT_LIGHT_INT = 0;
    public static final int IWEIGHT_MEDIUM_INT = 1;
    public static final int IWEIGHT_HEAVY_INT = 2;

    /**
     * 物品的类型
     */
    public static final String GOODTYPE_EXPRESS = "快递";
    public static final String GOODTYPE_FOOG = "餐饮";
    public static final String GOODTYPE_PAPER = "纸质";
    public static final String GOODTYPE_OTHER = "其它";


    private String id;
    private String type;
    private String imgUrl;
    private String weight;
    private String reward;
    private String startPlace;
    private String endPlace;
    private String limitTime;
    private String publicTime;
    private String state;
    private String des;
    private int currPage;


    public RewardBean() {
    }

    public RewardBean(int currPage, String des, String endPlace, String id, String imgUrl,
                      String limitTime, String publicTime, String reward,
                      String startPlace, String state, String type, String weight) {
        this.currPage = currPage;
        this.des = des;
        this.endPlace = endPlace;
        this.id = id;
        this.imgUrl = imgUrl;
        this.limitTime = limitTime;
        this.publicTime = publicTime;
        this.reward = reward;
        this.startPlace = startPlace;
        this.state = state;
        this.type = type;
        this.weight = weight;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int getCurrPage() {
        return currPage;
    }

    public String getEndPlace() {
        return endPlace;
    }

    public String getId() {
        return id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getLimitTime() {
        return limitTime;
    }

    public String getPublicTime() {
        return publicTime;
    }

    public String getReward() {
        return reward;
    }

    public String getStartPlace() {
        return startPlace;
    }

    public String getState() {
        return state;
    }

    public String getType() {
        return type;
    }

    public String getWeight() {
        return weight;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public void setEndPlace(String endPlace) {
        this.endPlace = endPlace;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLimitTime(String limitTime) {
        this.limitTime = limitTime;
    }

    public void setPublicTime(String publicTime) {
        this.publicTime = publicTime;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public void setStartPlace(String startPlace) {
        this.startPlace = startPlace;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public static RewardBean getInstance(Context context, RewardDeliveries deliveries, int pageIndex) {
        RewardBean rewardBean = new RewardBean();
        rewardBean.setId(deliveries.get_id());
        rewardBean.setType(deliveries.getThing().getType());
        rewardBean.setImgUrl(deliveries.getThing().getThumbnail());
        rewardBean.setWeight(RewardBean.getWeightByIndex(context, deliveries.getThing().getWeight()));
        rewardBean.setReward(deliveries.getReward() + "");
        rewardBean.setStartPlace(deliveries.getSource());
        rewardBean.setEndPlace(deliveries.getDestination());
        rewardBean.setLimitTime(deliveries.getDeadline().substring(5));
        rewardBean.setPublicTime(deliveries.getTime().substring(5,16));
        rewardBean.setState(deliveries.getState() + "");
        rewardBean.setCurrPage(pageIndex);
        return rewardBean;
    }

    public static String getWeightByIndex(Context context, String weight) {
        int index = Integer.parseInt(weight);
        String desireweight = context.getResources().getString(R.string.weight_middle);
        switch (index) {
            case 0:
                desireweight = context.getResources().getString(R.string.weight_light);
                break;
            case 1:
                desireweight = context.getResources().getString(R.string.weight_middle);
                break;
            case 2:
                desireweight = context.getResources().getString(R.string.weight_heavy);
                break;
        }
        return desireweight;
    }

    public static void setImg(String url, ImageView imageView, String type) {
        //类型是快递的显示的图片
        if (type.equals(GOODTYPE_EXPRESS)) {
            //图片的选项
            ImageOptions options = new ImageOptions.Builder()
                    .setImageScaleType(ImageView.ScaleType.FIT_XY)
                    .setCircular(true)
                    .setFailureDrawableId(R.drawable.good_type_express)//设置加载失败的图片
                    .setLoadingDrawableId(R.drawable.good_type_express)//设置加载中的图片
                    .build();
            x.image().bind(imageView, url, options);
            return;
        }
        //类型是餐饮的显示的图片
        if (type.equals(GOODTYPE_FOOG)) {
            //图片的选项
            ImageOptions options = new ImageOptions.Builder()
                    .setImageScaleType(ImageView.ScaleType.FIT_XY)
                    .setCircular(true)
                    .setFailureDrawableId(R.drawable.good_type_food)//设置加载失败的图片
                    .setLoadingDrawableId(R.drawable.good_type_food)//设置加载中的图片
                    .build();
            x.image().bind(imageView, url, options);
            return;
        }
        //类型是纸质的显示的图片
        if (type.equals(GOODTYPE_PAPER)) {
            //图片的选项
            ImageOptions options = new ImageOptions.Builder()
                    .setImageScaleType(ImageView.ScaleType.FIT_XY)
                    .setCircular(true)
                    .setFailureDrawableId(R.drawable.good_type_paper)//设置加载失败的图片
                    .setLoadingDrawableId(R.drawable.good_type_paper)//设置加载中的图片
                    .build();
            x.image().bind(imageView, url, options);
            return;
        }

        //类型是其它的显示的图片
        if (type.equals(GOODTYPE_OTHER)) {
            //图片的选项
            ImageOptions options = new ImageOptions.Builder()
                    .setImageScaleType(ImageView.ScaleType.FIT_XY)
                    .setCircular(true)
                    .setFailureDrawableId(R.drawable.good_type_other)//设置加载失败的图片
                    .setLoadingDrawableId(R.drawable.good_type_other)//设置加载中的图片
                    .build();
            x.image().bind(imageView, url, options);
            return;
        }
    }
}
