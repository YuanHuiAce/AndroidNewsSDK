package com.news.sdk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.news.sdk.R;
import com.news.sdk.entity.ChannelItem;
import com.news.sdk.utils.ImageUtil;
import com.news.sdk.utils.TextUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by fengjigang on 15/10/28.
 * 新闻频道可拖拽gridview 适配器
 */
public class ChannelSelectedAdapter extends BaseAdapter {
    /**
     * TAG
     */
    private final static String TAG = "DragAdapter";
    /**
     * 是否显示底部的ITEM
     */
    private boolean isItemShow = false;
    private Context context;
    /**
     * 控制的postion
     */
    private int holdPosition;
    /**
     * 是否改变
     */
    private boolean isChanged = false;
    /**
     * 是否可见
     */
    boolean isVisible = true;
    /**
     * 可以拖动的列表（即用户选择的频道列表）
     */
    public List<ChannelItem> channelList;
    /**
     * 要删除的position
     */
    public int remove_position = -1;

    public ChannelSelectedAdapter(Context context, List<ChannelItem> channelList) {
        this.context = context;
        this.channelList = channelList;
    }

    @Override
    public int getCount() {
        return channelList == null ? 0 : channelList.size();
    }

    @Override
    public ChannelItem getItem(int position) {
        if (channelList != null && channelList.size() != 0) {
            return channelList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(context).inflate(R.layout.subscribe_category_item, null);
            holder.tvItem = (TextView) convertView.findViewById(R.id.text_item);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.icon_new);
            ImageUtil.setAlphaImage(holder.ivIcon);
            TextUtil.setLayoutBgResource(context, holder.ivIcon, R.drawable.bg_channel_add);
            int height = context.getResources().getDrawable(R.drawable.bg_channel_add).getMinimumHeight();
            RelativeLayout.LayoutParams rlItemText = (RelativeLayout.LayoutParams) holder.tvItem.getLayoutParams();
            rlItemText.topMargin = height / 2;
            holder.tvItem.setLayoutParams(rlItemText);
            TextUtil.setLayoutBgResource(context, holder.tvItem, R.drawable.subscribe_item_bg);
            TextUtil.setTextColor(context, holder.tvItem, R.color.color2);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        ChannelItem channel = getItem(position);
        holder.tvItem.setText(channel.getCname());
        if (position == 0) {
            TextUtil.setTextColor(context, holder.tvItem, R.color.color3);
            holder.tvItem.setEnabled(false);
            holder.ivIcon.setVisibility(View.GONE);
        } else {
            TextUtil.setTextColor(context, holder.tvItem, R.color.color2);
            holder.ivIcon.setVisibility(View.VISIBLE);
        }
        if (isChanged && (position == holdPosition) && !isItemShow) {
            holder.tvItem.setText("");
            holder.tvItem.setSelected(true);
            holder.tvItem.setEnabled(true);
            isChanged = false;
            holder.ivIcon.setVisibility(View.GONE);
        }
        if (!isVisible && (position == -1 + channelList.size())) {
            holder.tvItem.setText("");
            holder.tvItem.setSelected(true);
            holder.tvItem.setEnabled(true);
            holder.ivIcon.setVisibility(View.GONE);
        }
        if (remove_position == position) {
            holder.tvItem.setText("");
            holder.tvItem.setSelected(true);
            holder.tvItem.setEnabled(true);
            holder.ivIcon.setVisibility(View.GONE);
        }
        return convertView;
    }

    /**
     * 添加频道列表
     */
    public void addItem(ChannelItem channel) {
        channelList.add(channel);
        notifyDataSetChanged();
    }

    /**
     * 拖动变更频道排序
     */
    public void exchange(int dragPostion, int dropPostion) {
        holdPosition = dropPostion;
        ChannelItem dragItem = getItem(dragPostion);
        if (dragPostion < dropPostion) {
            channelList.add(dropPostion + 1, dragItem);
            channelList.remove(dragPostion);
        } else {
            channelList.add(dropPostion, dragItem);
            channelList.remove(dragPostion + 1);
        }
        isChanged = true;
        notifyDataSetChanged();
    }

    /**
     * 获取频道列表
     */
    public List<ChannelItem> getChannnelLst() {
        return channelList;
    }

    /**
     * 获取频道列表
     */
    public ArrayList<ChannelItem> getChannnelList() {
        if (channelList != null) {
            return (ArrayList<ChannelItem>) channelList;
        }
        return null;
    }

    /**
     * 设置删除的position
     */
    public synchronized void setRemove(int position) {
        remove_position = position;
        notifyDataSetChanged();
    }

    /**
     * 删除频道列表
     */
    public synchronized void remove() {
        synchronized (channelList) {
            channelList.remove(remove_position);
            remove_position = -1;
            notifyDataSetChanged();
        }
    }

    /**
     * 设置频道列表
     */
    public void setListData(List<ChannelItem> list) {
        channelList = list;
    }

    /**
     * 获取是否可见
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * 设置是否可见
     */
    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    /**
     * 显示放下的ITEM
     */
    public void setShowDropItem(boolean show) {
        isItemShow = show;
    }

    class Holder {
        TextView tvItem;
        ImageView ivIcon;
    }
}