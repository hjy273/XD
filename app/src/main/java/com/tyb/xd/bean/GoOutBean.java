package com.tyb.xd.bean;

import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.tyb.xd.R;
import com.tyb.xd.fastbean.GoOutDeliveries;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.Serializable;

/**
 * 出行列表
 */
public class GoOutBean implements Serializable {
    private String id;
    private String userName;
    private String headimgUrl;
    private String reward;
    private String startPlace;
    private String endPlace;
    private String limitTime;
    private String publicTime;
    private String state;
    private int currPage;

    public GoOutBean() {
    }

    public GoOutBean(int currPage, String endPlace, String headimgUrl, String id,
                     String limitTime, String publicTime,
                     String startPlace, String reward, String state,
                     String userName) {
        this.currPage = currPage;
        this.endPlace = endPlace;
        this.headimgUrl = headimgUrl;
        this.id = id;
        this.limitTime = limitTime;
        this.publicTime = publicTime;
        this.startPlace = startPlace;
        this.reward = reward;
        this.state = state;
        this.userName = userName;
    }

    public int getCurrPage() {
        return currPage;
    }

    public String getEndPlace() {
        return endPlace;
    }

    public String getHeadimgUrl() {
        return headimgUrl;
    }

    public String getId() {
        return id;
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

    public String getUserName() {
        return userName;
    }

    public String getState() {
        return state;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public void setEndPlace(String endPlace) {
        this.endPlace = endPlace;
    }

    public void setHeadimgUrl(String headimgUrl) {
        this.headimgUrl = headimgUrl;
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

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setState(String state) {
        this.state = state;
    }

    public static GoOutBean getInstance(GoOutDeliveries deliveries, int pageIndex) {
        GoOutBean outBean = new GoOutBean();
        outBean.setId(deliveries.get_id());
        outBean.setUserName(deliveries.getUser().getUsername());
        outBean.setHeadimgUrl(deliveries.getUser().getHeadimg());
        outBean.setReward(deliveries.getReward()+"");
        outBean.setStartPlace(deliveries.getSource());
        outBean.setEndPlace(deliveries.getDestination());
        outBean.setLimitTime(deliveries.getDeadline().substring(5));
        outBean.setPublicTime(deliveries.getTime().substring(5,16));
        outBean.setState(deliveries.getState()+"");
        outBean.setCurrPage(pageIndex);
        return outBean;
    }
    public static void setImg(ImageView img,String url)
    {
        //图片的选项
        ImageOptions options = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setCircular(true)
                .setFailureDrawableId(R.drawable.good_type_express)//设置加载失败的图片
                .setLoadingDrawableId(R.drawable.good_type_express)//设置加载中的图片
                .build();
        x.image().bind(img,url,options);
    }
}