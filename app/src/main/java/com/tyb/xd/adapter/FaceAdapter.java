package com.tyb.xd.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.tyb.xd.R;
import com.tyb.xd.utils.FaceUtil;
import com.tyb.xd.utils.Util;
import com.tyb.xd.view.GifTextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @desc:表情展示的gridview的adapter
 */
public class FaceAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int currentPage = 0;
    private Map<String, Integer> mFaceMap;
    private List<Integer> faceList = new ArrayList<Integer>();// 存放表情资源的list
    private Context mContext;
    private Resources mResources;
    private int mCount = 0;

    public FaceAdapter(Context context, int currentPage) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.currentPage = currentPage;
        mResources = mContext.getResources();
        mFaceMap = FaceUtil.getmFaceMap();
        mCount = FaceUtil.getmFaceMap().size() - currentPage * 20;
        initData();
    }

    private void initData() {
        for (Map.Entry<String, Integer> entry : mFaceMap.entrySet()) {
            faceList.add(entry.getValue());
        }
    }

    @Override
    public int getCount() {
        return 21;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.chat_face_item, null, false);
            viewHolder.faceTV = (GifTextView) convertView
                    .findViewById(R.id.face_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position == 20) {
            /**
             * 每一个pager的最后一个默认为删除键
             * 可以删除已经选择的图片
             */
            Bitmap bitmap = BitmapFactory.decodeResource(
                    mContext.getResources(), R.drawable.emotion_del_normal);
            if (bitmap != null) {
                int rawHeigh = bitmap.getHeight();
                int rawWidth = bitmap.getHeight();
                // 设置表情的大小===
                int newHeight = Util.dip2px(mContext, 30);
                int newWidth = Util.dip2px(mContext, 30);
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
                ImageSpan imageSpan = new ImageSpan(mContext,
                        newBitmap);
                //图片的名
                String emojiStr = "删除";
                //需要处理的文本，[smile]是需要被替代的文本
                SpannableString spannableString = new SpannableString(
                        emojiStr);
                //开始替换，注意第2和第3个参数表示从哪里开始替换到哪里替换结束（start和end）
                //最后一个参数类似数学中的集合,[5,12)表示从5到12，包括5但不包括12
                //所以在这里是       emojiStr.indexOf(']') + 1,
                spannableString.setSpan(imageSpan, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                viewHolder.faceTV.setText(spannableString);
            }

        } else {
            int count = 20 * currentPage + position;
            // 总共107个表情==
            if (count < 107) {
                viewHolder.faceTV.setText(FaceUtil.convertStringToSpanableString((String) mFaceMap.keySet().toArray()[count],
                        (int) (FaceUtil.getmFaceMap().values().toArray())[count], mContext));
            } else {
            }
        }
        return convertView;
    }

    public static class ViewHolder {
        GifTextView faceTV;
    }
}
