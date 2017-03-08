package com.tyb.xd.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.throrinstudio.android.common.libs.widgets.MResource;
import com.tyb.xd.R;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangpeiyu on 2016/7/2.
 */
public class FaceUtil {

    public static Map<String, Integer> mFaceMap = new LinkedHashMap<String, Integer>();
    public static final Pattern EMOTION_URL = Pattern.compile("\\[(\\S+?)\\]");
    /**
     * 返回表情集合
     *
     * @return
     */
    public static Map<String, Integer> getmFaceMap() {
        if (!mFaceMap.isEmpty()) ;
        else {
            initMap();
        }
        return mFaceMap;
    }

    /**
     * 初始化
     */
    private static void initMap() {
        // TODO Auto-generated method stub
        /**
         * 第一面
         */
        mFaceMap.put("[呲牙]", R.drawable.f000);
        mFaceMap.put("[调皮]", R.drawable.f001);
        mFaceMap.put("[流汗]", R.drawable.f002);
        mFaceMap.put("[偷笑]", R.drawable.f003);
        mFaceMap.put("[再见]", R.drawable.f004);
        mFaceMap.put("[擦汗]", R.drawable.f006);
        mFaceMap.put("[流泪]", R.drawable.f009);
        mFaceMap.put("[大哭]", R.drawable.f010);
        mFaceMap.put("[嘘]", R.drawable.f011);
        mFaceMap.put("[酷]", R.drawable.f012);
        mFaceMap.put("[抓狂]", R.drawable.f013);
        mFaceMap.put("[委屈]", R.drawable.f014);
        mFaceMap.put("[便便]", R.drawable.f015);
        mFaceMap.put("[菜刀]", R.drawable.f017);
        mFaceMap.put("[可爱]", R.drawable.f018);
        mFaceMap.put("[色]", R.drawable.f019);
        mFaceMap.put("[害羞]", R.drawable.f020);
/**
 * 第二面
 */
        mFaceMap.put("[得意]", R.drawable.f021);
        mFaceMap.put("[吐]", R.drawable.f022);
        mFaceMap.put("[微笑]", R.drawable.f023);
        mFaceMap.put("[发怒]", R.drawable.f024);
        mFaceMap.put("[尴尬]", R.drawable.f025);
        mFaceMap.put("[惊恐]", R.drawable.f026);
        mFaceMap.put("[冷汗]", R.drawable.f027);
        mFaceMap.put("[示爱]", R.drawable.f029);
        mFaceMap.put("[白眼]", R.drawable.f030);
        mFaceMap.put("[傲慢]", R.drawable.f031);
        mFaceMap.put("[惊讶]", R.drawable.f033);
        mFaceMap.put("[疑问]", R.drawable.f034);
        mFaceMap.put("[睡]", R.drawable.f035);
        mFaceMap.put("[亲亲]", R.drawable.f036);
        mFaceMap.put("[憨笑]", R.drawable.f037);
        mFaceMap.put("[爱情]", R.drawable.f038);
        mFaceMap.put("[撇嘴]", R.drawable.f040);
        mFaceMap.put("[阴险]", R.drawable.f041);
/**
 * 第三面
 */
        mFaceMap.put("[奋斗]", R.drawable.f042);
        mFaceMap.put("[发呆]", R.drawable.f043);
        mFaceMap.put("[右哼哼]", R.drawable.f044);
        mFaceMap.put("[拥抱]", R.drawable.f045);
        mFaceMap.put("[坏笑]", R.drawable.f046);
        mFaceMap.put("[鄙视]", R.drawable.f048);
        mFaceMap.put("[晕]", R.drawable.f049);
        mFaceMap.put("[大兵]", R.drawable.f050);
        mFaceMap.put("[可怜]", R.drawable.f051);
        mFaceMap.put("[强]", R.drawable.f052);
        mFaceMap.put("[弱]", R.drawable.f053);
        mFaceMap.put("[握手]", R.drawable.f054);
        mFaceMap.put("[胜利]", R.drawable.f055);
        mFaceMap.put("[抱拳]", R.drawable.f056);
        mFaceMap.put("[蛋糕]", R.drawable.f059);
        mFaceMap.put("[啤酒]", R.drawable.f061);
        mFaceMap.put("[飘虫]", R.drawable.f062);
/**
 * 第四面
 */
        mFaceMap.put("[勾引]", R.drawable.f063);
        mFaceMap.put("[爱你]", R.drawable.f065);
        mFaceMap.put("[咖啡]", R.drawable.f066);
        mFaceMap.put("[刀]", R.drawable.f070);
        mFaceMap.put("[发抖]", R.drawable.f071);
        mFaceMap.put("[差劲]", R.drawable.f072);
        mFaceMap.put("[拳头]", R.drawable.f073);
        mFaceMap.put("[心碎]", R.drawable.f074);
        mFaceMap.put("[足球]", R.drawable.f077);
        mFaceMap.put("[挥手]", R.drawable.f079);
        mFaceMap.put("[饥饿]", R.drawable.f081);
        mFaceMap.put("[困]", R.drawable.f082);
        mFaceMap.put("[咒骂]", R.drawable.f083);
/**
 * 第五面
 */
        mFaceMap.put("[折磨]", R.drawable.f084);
        mFaceMap.put("[抠鼻]", R.drawable.f085);
        mFaceMap.put("[鼓掌]", R.drawable.f086);
        mFaceMap.put("[糗大了]", R.drawable.f087);
        mFaceMap.put("[左哼哼]", R.drawable.f088);
        mFaceMap.put("[哈欠]", R.drawable.f089);
        mFaceMap.put("[快哭了]", R.drawable.f090);
        mFaceMap.put("[吓]", R.drawable.f091);
        mFaceMap.put("[篮球]", R.drawable.f092);
        mFaceMap.put("[乒乓球]", R.drawable.f093);
        mFaceMap.put("[NO]", R.drawable.f094);
        mFaceMap.put("[跳跳]", R.drawable.f095);
        mFaceMap.put("[怄火]", R.drawable.f096);
        mFaceMap.put("[转圈]", R.drawable.f097);
        mFaceMap.put("[磕头]", R.drawable.f098);
        mFaceMap.put("[回头]", R.drawable.f099);
        mFaceMap.put("[跳绳]", R.drawable.f100);
        mFaceMap.put("[激动]", R.drawable.f101);
        mFaceMap.put("[街舞]", R.drawable.f102);
        mFaceMap.put("[献吻]", R.drawable.f103);
        mFaceMap.put("[左太极]", R.drawable.f104);
        mFaceMap.put("[右太极]", R.drawable.f105);
        mFaceMap.put("[闭嘴]", R.drawable.f106);
    }


    /**
     * @param facename
     * @param id
     * @return
     */
    public static SpannableString convertStringToSpanableString(String facename, int id, Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), id);
        if (bitmap != null) {
            int rawHeigh = bitmap.getHeight();
            int rawWidth = bitmap.getHeight();
            // 设置表情的大小===
            int newHeight = Util.dip2px(context, 30);
            int newWidth = Util.dip2px(context, 30);
            // 计算缩放因子
            float heightScale = ((float) newHeight) / rawHeigh;
            float widthScale = ((float) newWidth) / rawWidth;
            // 新建立矩阵
            Matrix matrix = new Matrix();
            matrix.postScale(widthScale, heightScale);
            // 设置图片的旋转角度
            // matrix.postRotate(-30);
            // 设置图片的倾斜
            // matrix.postSkew(0.1f, 0.1f);
            // 将图片大小压缩
            // 压缩后图片的宽和高以及kB大小均会变化
            Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    rawWidth, rawHeigh, matrix, true);
            //要让图片替代指定的文字就要用ImageSpan
            ImageSpan imageSpan = new ImageSpan(context,
                    newBitmap);
            //图片的名
            String emojiStr = facename;
            //需要处理的文本，[smile]是需要被替代的文本
            SpannableString spannableString = new SpannableString(
                    emojiStr);
            //开始替换，注意第2和第3个参数表示从哪里开始替换到哪里替换结束（start和end）
            //最后一个参数类似数学中的集合,[5,12)表示从5到12，包括5但不包括12
            //所以在这里是       emojiStr.indexOf(']') + 1,
            spannableString.setSpan(imageSpan,
                    emojiStr.indexOf('['),
                    emojiStr.indexOf(']') + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }
        return null;
    }


    /**
     * 另外一种方法解析表情将[表情]换成fxxx
     *
     * @param message 传入的需要处理的String
     * @return
     */
    public static String convertNormalStringToSpannableString(String message, Context context) {
        String hackTxt;
        if (message.startsWith("[") && message.endsWith("]")) {
            hackTxt = message + " ";
        } else {
            hackTxt = message;
        }

        Matcher localMatcher = EMOTION_URL.matcher(hackTxt);
        while (localMatcher.find()) {
            String str2 = localMatcher.group(0);
            if (FaceUtil.getmFaceMap().containsKey(str2)) {
                String faceName = context.getResources().getString(
                        FaceUtil.getmFaceMap().get(str2));
                CharSequence name = options(faceName);
                SpannableString r = convertStringToSpanableString(str2, MResource.getIdByName(context, "drawable", name.toString()), context);
                message = message.replace(str2, name);
            }
        }
        return message;
    }

    /**
     * 取名字f010
     *
     * @param faceName
     */
    public static CharSequence options(String faceName) {
        int start = faceName.lastIndexOf("/");
        CharSequence c = faceName.subSequence(start + 1, faceName.length() - 4);
        return c;
    }
}
