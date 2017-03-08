package com.tyb.xd.utils;

import android.util.Base64;

/**
 * 加解密实现
 */
public class SecurityUtils {

    public static String encode(String src) {
        String encrypt1 = Base64.encodeToString(src.getBytes(), Base64.DEFAULT);
        encrypt1 = encrypt1 + "TRYYOUBESTXD";
        String encrypt2 = SecurityUtils.reverse(encrypt1);
        return encrypt2;
    }


    public static String reverse(String s) {
        int length = s.length();
        if (length <= 1)
            return s;
        String left = s.substring(0, length / 2);
        String right = s.substring(length / 2, length);
        return SecurityUtils.reverse(right) + SecurityUtils.reverse(left);
    }

    public static String decode(String src) {
        src = SecurityUtils.reverse(src);
        src = src.substring(0, src.length() - 12);
        src = new String(Base64.decode(src, Base64.DEFAULT));
        return src;
    }
}
