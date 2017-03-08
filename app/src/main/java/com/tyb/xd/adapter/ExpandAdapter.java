package com.tyb.xd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tyb.xd.R;
import com.tyb.xd.bean.CarryRecordBean;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 作者：long2ice on 2016/08/01 16:12
 * 邮箱：343178315@qq.com
 */
public class ExpandAdapter extends BaseExpandableListAdapter {

    private List<List<CarryRecordBean>> mList;
    private LayoutInflater mInflater;
    private Context mContext;

    public ExpandAdapter(Context context, List<List<CarryRecordBean>> list) {
        mInflater = LayoutInflater.from(context);
        this.mList = list;
        this.mContext = context;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return mList.get(groupPosition).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public int getChildrenCount(int groupPosition) {
        return mList.get(groupPosition).size();
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.carry_record_item, null);
        ViewHolder holder = new ViewHolder();
        x.view().inject(holder, convertView);
        //设置tag，便于在长按的时候找到
        convertView.setTag(R.id.bottom,groupPosition);
        convertView.setTag(R.id.center,childPosition);
        //图片的选项
        ImageOptions options = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setCircular(true)
                .setFailureDrawableId(R.drawable.good_type_express)//设置加载失败的图片
                .setLoadingDrawableId(R.drawable.good_type_express)//设置加载中的图片
                .build();
        x.image().bind(holder.mivGoodImg, mList.get(groupPosition).get(childPosition).getImg(), options);
        holder.mtvStartPlace.setText(mList.get(groupPosition).get(childPosition).getSendStartPlace());
        holder.mtvArrivePlace.setText(mList.get(groupPosition).get(childPosition).getSendArrivePlace());
        holder.mtvTypeOrName.setText(mList.get(groupPosition).get(childPosition).getTypeOrName());
        holder.mtvTime.setText(mList.get(groupPosition).get(childPosition).getSendRecordTime());
        String state = "";
        switch (mList.get(groupPosition).get(childPosition).getSendRecordStatus()) {
            case "0":
                state = "已删除";
                break;
            case "1":
                state = "已失效";
                holder.mtvState.setTextColor(mContext.getResources().getColor(R.color.crimson));
                break;
            case "2":
                state = "未领取";
                holder.mtvState.setTextColor(mContext.getResources().getColor(R.color.orange));
                break;
            case "3":
                state = "已领取";
                holder.mtvState.setTextColor(mContext.getResources().getColor(R.color.chartreuse));
                break;
            case "4":
                state = "已发出";
                holder.mtvState.setTextColor(mContext.getResources().getColor(R.color.darkviolet));
                break;
            case "5":
                state = "已送达";
                holder.mtvState.setTextColor(mContext.getResources().getColor(R.color.blue));
                break;
            case "6":
                state = "已完成";
                holder.mtvState.setTextColor(mContext.getResources().getColor(R.color.dodgerblue));
                break;
        }
        holder.mtvState.setText(state);
        return convertView;
    }

    public Object getGroup(int groupPosition) {
        return mList.get(groupPosition);
    }

    public int getGroupCount() {
        return mList.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.carry_record_group_item, null);
        }
        TextView type = (TextView) convertView.findViewById(R.id.id_ac_recode_tv_record_type);
        //设置tag，便于在长按的时候找到
        convertView.setTag(R.id.bottom,groupPosition);
        convertView.setTag(R.id.center,-1);

        if (groupPosition == 0) {
            type.setText("悬赏记录");
        } else {
            type.setText("出行记录");
        }
        type.setTextColor(mContext.getResources().getColor(R.color.text_color_thin_black));
        type.setTextSize(16);
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class ViewHolder {
        @ViewInject(R.id.id_recode_iv_img)
        private ImageView mivGoodImg;
        @ViewInject(R.id.id_recode_tv_name)
        private TextView mtvTypeOrName;
        @ViewInject(R.id.id_recode_tv_start_place)
        private TextView mtvStartPlace;
        @ViewInject(R.id.id_recode_tv_arrive_place)
        private TextView mtvArrivePlace;
        @ViewInject(R.id.id_recode_tv_time)
        private TextView mtvTime;
        @ViewInject(R.id.id_recode_tv_state)
        private TextView mtvState;
    }
}
