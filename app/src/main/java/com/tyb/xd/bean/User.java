package com.tyb.xd.bean;

import java.io.Serializable;

/**
 * 普通用户类
 */
public class User implements Serializable {
    //手机号
    protected String mUserPhone = "";
    //密码
    protected String mUserPass = "";
    //Token
    protected String mToken = "";

    /**
     * 认证人类型
     */
    public static enum IdentificateType {
        Student, Teacher;
    }

    /**
     * 性别
     */
    public static enum Sex {
        Male, Female;
    }

    //用户名
    protected String mUsername = "";
    //用户的昵称
    protected String mNickname = "";
    //头像本地路径
    protected String mUserPath = "";
    //头像网络路径
    protected String mUserUrl = "";
    //学号
    protected String mSchoolId = "";
    //判断是否认证
    protected boolean mIsIdentificate = false;
    //认证类型
    protected IdentificateType mIdentificateType = IdentificateType.Student;
    //金笑点
    protected float mGoldMoney = 0f;
    //银笑点
    protected float mSilverMoney = 0f;
    //信誉度
    protected float mCreditibility = 0f;
    //学校
    protected String mSchool = "";
    //校区
    protected String mCampus = "";
    //性别
    protected Sex mSex = Sex.Male;
    //个性签名
    protected String mSign = "";
    //用户的角色
    protected String mRole = "";


    public User() {
    }

    public User(String mUserPhone, String mUserPass, String mUsername, String mUserPath, String headurl,
                float mSilverMoney, String mSign, Sex mSex, String mSchool, String campus, String mSchoolId,
                boolean mIsIdentificate, IdentificateType mIdentificateType, float mGoldMoney,
                float mCreditibility, String token, String role) {
        this.mUserPhone = mUserPhone;
        this.mUserPass = mUserPass;
        this.mUsername = mUsername;
        this.mUserPath = mUserPath;
        this.mUserUrl = headurl;
        this.mSilverMoney = mSilverMoney;
        this.mSign = mSign;
        this.mSex = mSex;
        this.mSchool = mSchool;
        this.mCampus = campus;
        this.mSchoolId = mSchoolId;
        this.mIsIdentificate = mIsIdentificate;
        this.mIdentificateType = mIdentificateType;
        this.mGoldMoney = mGoldMoney;
        this.mCreditibility = mCreditibility;
        this.mToken = token;
        this.mRole = role;
    }

    public String getmNickname() {
        return mNickname;
    }

    public void setmNickname(String mNickname) {
        this.mNickname = mNickname;
    }


    public String getmUserUrl() {
        return mUserUrl;
    }

    public void setmUserUrl(String mUserUrl) {
        this.mUserUrl = mUserUrl;
    }

    public String getmCampus() {
        return mCampus;
    }

    public void setmCampus(String mCampus) {
        this.mCampus = mCampus;
    }

    public String getmUserPhone() {
        return mUserPhone;
    }

    public String getmUserPath() {
        return mUserPath;
    }

    public String getmUserPass() {
        return mUserPass;
    }

    public String getmUsername() {
        return mUsername;
    }

    public float getmSilverMoney() {
        return mSilverMoney;
    }

    public String getmRole() {
        return mRole;
    }

    public void setmRole(String mRole) {
        this.mRole = mRole;
    }

    public String getmSign() {
        return mSign;
    }

    public Sex getmSex() {
        return mSex;
    }

    public String getmSchoolId() {
        return mSchoolId;
    }

    public String getmSchool() {
        return mSchool;
    }

    public boolean ismIsIdentificate() {
        return mIsIdentificate;
    }

    public IdentificateType getmIdentificateType() {
        return mIdentificateType;
    }

    public float getmGoldMoney() {
        return mGoldMoney;
    }

    public float getmCreditibility() {
        return mCreditibility;
    }

    public void setmCreditibility(float mCreditibility) {
        this.mCreditibility = mCreditibility;
    }

    public void setmGoldMoney(float mGoldMoney) {
        this.mGoldMoney = mGoldMoney;
    }

    public void setmIdentificateType(IdentificateType mIdentificateType) {
        this.mIdentificateType = mIdentificateType;
    }

    public void setmSchool(String mSchool) {
        this.mSchool = mSchool;
    }

    public void setmIsIdentificate(boolean mIsIdentificate) {
        this.mIsIdentificate = mIsIdentificate;
    }

    public void setmSchoolId(String mSchoolId) {
        this.mSchoolId = mSchoolId;
    }

    public void setmSex(Sex mSex) {
        this.mSex = mSex;
    }

    public void setmSign(String mSign) {
        this.mSign = mSign;
    }

    public void setmSilverMoney(float mSilverMoney) {
        this.mSilverMoney = mSilverMoney;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public void setmUserPass(String mUserPass) {
        this.mUserPass = mUserPass;
    }

    public void setmUserPath(String mUserPath) {
        this.mUserPath = mUserPath;
    }

    public void setmUserPhone(String mUserPhone) {
        this.mUserPhone = mUserPhone;
    }

    public String getmToken() {
        return mToken;
    }

    public void setmToken(String mToken) {
        this.mToken = mToken;
    }

    public void resetInfo() {
        mUserPhone = "";
        mUserPass = "";
        mToken = "";
        mUsername = "";
        mUserPath = "";
        mUserUrl = "";
        mSchoolId = "";
        mIsIdentificate = false;
        mIdentificateType = IdentificateType.Student;
        mGoldMoney = 0f;
        mSilverMoney = 0f;
        mCreditibility = 0f;
        mSchool = "";
        mCampus = "";
        mSex = Sex.Male;
        mSign = "";
        mRole = "";
    }
}
