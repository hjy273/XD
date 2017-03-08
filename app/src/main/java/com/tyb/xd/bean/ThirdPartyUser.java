package com.tyb.xd.bean;

import android.widget.TextView;

/**
 * 第三方登录账户
 */
public class ThirdPartyUser extends User {
    //第三方登录id
    protected String mThirdPartyId;

    public ThirdPartyUser() {
    }

    public ThirdPartyUser(String mUserPhone, String mUserPass, String mUsername, String mUserPath,String headurl,
                          float mSilverMoney, String mSign, Sex mSex, String mSchool, String campus,
                          String mSchoolId, boolean mIsIdentificate, IdentificateType mIdentificateType,
                          float mGoldMoney, float mCreditibility, String mAccess_Token, String mThirdPartyId, String token,String role) {
        super(mUserPhone, mUserPass, mUsername, mUserPath, headurl,mSilverMoney, mSign,
                mSex, mSchool, campus, mSchoolId, mIsIdentificate, mIdentificateType, mGoldMoney, mCreditibility, token,role);
        this.mThirdPartyId = mThirdPartyId;
    }


    public String getmThirdPartyId() {
        return mThirdPartyId;
    }


    public void setmThirdPartyId(String mThirdPartyId) {
        this.mThirdPartyId = mThirdPartyId;
    }
}
